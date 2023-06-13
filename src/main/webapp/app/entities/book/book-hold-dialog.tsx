import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';

import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, holdBook } from './book.reducer';

export const BookHoldDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const bookEntity = useAppSelector(state => state.book.entity);
  const updateSuccess = useAppSelector(state => state.book.updateSuccess);

  const handleClose = () => {
    navigate('/book' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmHold = () => {
    dispatch(holdBook(bookEntity.id));
    navigate('/book' + location.search);
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="bookDeleteDialogHeading">
        Confirm hold book
      </ModalHeader>
      <ModalBody id="libraryManagementApp.book.delete.question">Are you sure you want to hold book {bookEntity.title}?</ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          Cancel
        </Button>
        <Button id="jhi-confirm-hold-book" data-cy="entityConfirmHoldButton" color="primary" onClick={confirmHold}>
          Yes
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default BookHoldDialog;
