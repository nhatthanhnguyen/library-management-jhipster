import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { Button, Modal, ModalBody, ModalFooter, ModalHeader } from 'reactstrap';

import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntity, returnBook } from './checkout.reducer';

export const CheckoutReturnDialog = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();

  const [loadModal, setLoadModal] = useState(false);

  useEffect(() => {
    dispatch(getEntity(id));
    setLoadModal(true);
  }, []);

  const checkoutEntity = useAppSelector(state => state.checkout.entity);
  const updateSuccess = useAppSelector(state => state.checkout.updateSuccess);

  const handleClose = () => {
    navigate('/checkout' + location.search);
  };

  useEffect(() => {
    if (updateSuccess && loadModal) {
      handleClose();
      setLoadModal(false);
    }
  }, [updateSuccess]);

  const confirmReturn = () => {
    dispatch(returnBook(checkoutEntity.id));
    navigate('/checkout' + location.search);
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose} data-cy="checkoutDeleteDialogHeading">
        Confirm return
      </ModalHeader>
      <ModalBody id="libraryManagementApp.checkout.delete.question">
        Are you sure you want to return the book {checkoutEntity.bookCopy ? checkoutEntity.bookCopy.id : ''}?
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          Cancel
        </Button>
        <Button id="jhi-confirm-delete-checkout" data-cy="entityConfirmDeleteButton" color="danger" onClick={confirmReturn}>
          Accept
        </Button>
      </ModalFooter>
    </Modal>
  );
};

export default CheckoutReturnDialog;
