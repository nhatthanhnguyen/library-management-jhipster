import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './hold.reducer';

export const HoldDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const holdEntity = useAppSelector(state => state.hold.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="holdDetailsHeading">Hold</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{holdEntity.id}</dd>
          <dt>
            <span id="startTime">Start Time</span>
          </dt>
          <dd>{holdEntity.startTime ? <TextFormat value={holdEntity.startTime} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <span id="endTime">End Time</span>
          </dt>
          <dd>{holdEntity.endTime ? <TextFormat value={holdEntity.endTime} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>User</dt>
          <dd>{holdEntity.user ? holdEntity.user.id : ''}</dd>
          <dt>Book Copy</dt>
          <dd>{holdEntity.bookCopy ? holdEntity.bookCopy.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/hold" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/hold/${holdEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default HoldDetail;
