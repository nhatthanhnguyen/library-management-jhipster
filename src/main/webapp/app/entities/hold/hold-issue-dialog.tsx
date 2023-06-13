import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, deleteEntity, issueBook } from './hold.reducer';

export const HoldIssueDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const holdEntity = useAppSelector(state => state.hold.entity);
  const updateSuccess = useAppSelector(state => state.hold.updateSuccess);

  const handleClose = () => {
    navigate('/hold' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmIssue = () => {
    dispatch(issueBook(holdEntity.id));
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="holdDeleteDialogHeading">
        Confirm issue book
      </ModalHeader>
      <ModalBody id="libraryManagementApp.hold.delete.question">
        Are you sure you want to issue book {holdEntity.bookCopy ? holdEntity.bookCopy.id : ''}?
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          Cancel
        </Button>
        <Button id="jhi-confirm-delete-hold" data-cy="entityConfirmDeleteButton" color="primary" onClick={confirmIssue}>
          Accept
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default HoldIssueDialog;
