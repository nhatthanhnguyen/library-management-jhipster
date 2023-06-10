import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './checkout.reducer';

export const CheckoutDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const checkoutEntity = useAppSelector(state => state.checkout.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="checkoutDetailsHeading">Checkout</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{checkoutEntity.id}</dd>
          <dt>
            <span id="startTime">Start Time</span>
          </dt>
          <dd>
            {checkoutEntity.startTime ? <TextFormat value={checkoutEntity.startTime} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endTime">End Time</span>
          </dt>
          <dd>
            {checkoutEntity.endTime ? <TextFormat value={checkoutEntity.endTime} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="isReturned">Is Returned</span>
          </dt>
          <dd>{checkoutEntity.isReturned ? 'true' : 'false'}</dd>
          <dt>User</dt>
          <dd>{checkoutEntity.user ? checkoutEntity.user.login : ''}</dd>
          <dt>Book Copy</dt>
          <dd>{checkoutEntity.bookCopy ? checkoutEntity.bookCopy.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/checkout" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/checkout/${checkoutEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default CheckoutDetail;
