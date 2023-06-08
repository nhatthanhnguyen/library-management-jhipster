import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { IBookCopy } from 'app/shared/model/book-copy.model';
import { getEntities as getBookCopies } from 'app/entities/book-copy/book-copy.reducer';
import { ICheckout } from 'app/shared/model/checkout.model';
import { getEntity, updateEntity, createEntity, reset } from './checkout.reducer';

export const CheckoutUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const bookCopies = useAppSelector(state => state.bookCopy.entities);
  const checkoutEntity = useAppSelector(state => state.checkout.entity);
  const loading = useAppSelector(state => state.checkout.loading);
  const updating = useAppSelector(state => state.checkout.updating);
  const updateSuccess = useAppSelector(state => state.checkout.updateSuccess);

  const handleClose = () => {
    navigate('/checkout' + location.search);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
    dispatch(getBookCopies({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...checkoutEntity,
      ...values,
      user: users.find(it => it.id.toString() === values.user.toString()),
      bookCopy: bookCopies.find(it => it.id.toString() === values.bookCopy.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...checkoutEntity,
          user: checkoutEntity?.user?.id,
          bookCopy: checkoutEntity?.bookCopy?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="libraryManagementApp.checkout.home.createOrEditLabel" data-cy="CheckoutCreateUpdateHeading">
            Create or edit a Checkout
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="checkout-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField label="Start Time" id="checkout-startTime" name="startTime" data-cy="startTime" type="date" />
              <ValidatedField label="End Time" id="checkout-endTime" name="endTime" data-cy="endTime" type="date" />
              <ValidatedField label="Is Returned" id="checkout-isReturned" name="isReturned" data-cy="isReturned" check type="checkbox" />
              <ValidatedField id="checkout-user" name="user" data-cy="user" label="User" type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <ValidatedField id="checkout-bookCopy" name="bookCopy" data-cy="bookCopy" label="Book Copy" type="select">
                <option value="" key="0" />
                {bookCopies
                  ? bookCopies.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/checkout" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CheckoutUpdate;
