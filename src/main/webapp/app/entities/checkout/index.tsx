import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Checkout from './checkout';
import CheckoutDetail from './checkout-detail';
import CheckoutUpdate from './checkout-update';
import CheckoutDeleteDialog from './checkout-delete-dialog';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const CheckoutRoutes = () => {
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  return (
    <ErrorBoundaryRoutes>
      <Route index element={<Checkout />} />
      {isAdmin ? (
        <>
          <Route path="new" element={<CheckoutUpdate />} />
          <Route path=":id">
            <Route index element={<CheckoutDetail />} />
            <Route path="edit" element={<CheckoutUpdate />} />
            <Route path="delete" element={<CheckoutDeleteDialog />} />
          </Route>
        </>
      ) : undefined}
    </ErrorBoundaryRoutes>
  );
};

export default CheckoutRoutes;
