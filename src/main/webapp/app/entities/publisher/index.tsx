import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Publisher from './publisher';
import PublisherDetail from './publisher-detail';
import PublisherUpdate from './publisher-update';
import PublisherDeleteDialog from './publisher-delete-dialog';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const PublisherRoutes = () => {
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  return (
    <ErrorBoundaryRoutes>
      <Route index element={<Publisher />} />
      {isAdmin ? (
        <>
          <Route path="new" element={<PublisherUpdate />} />
          <Route path=":id">
            <Route index element={<PublisherDetail />} />
            <Route path="edit" element={<PublisherUpdate />} />
            <Route path="delete" element={<PublisherDeleteDialog />} />
          </Route>
        </>
      ) : undefined}
    </ErrorBoundaryRoutes>
  );
};

export default PublisherRoutes;
