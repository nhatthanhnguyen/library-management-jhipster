import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './wait-list.reducer';

export const WaitListDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const waitListEntity = useAppSelector(state => state.waitList.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="waitListDetailsHeading">Wait List</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{waitListEntity.id}</dd>
          <dt>User</dt>
          <dd>{waitListEntity.user ? waitListEntity.user.login : ''}</dd>
          <dt>Book</dt>
          <dd>{waitListEntity.book ? waitListEntity.book.title : ''}</dd>
        </dl>
        <Button tag={Link} to="/wait-list" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/wait-list/${waitListEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default WaitListDetail;
