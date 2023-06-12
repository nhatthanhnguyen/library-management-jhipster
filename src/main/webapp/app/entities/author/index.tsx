import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Author from './author';
import AuthorDetail from './author-detail';
import AuthorUpdate from './author-update';
import AuthorDeleteDialog from './author-delete-dialog';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const AuthorRoutes = () => {
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));

  return (
    <ErrorBoundaryRoutes>
      <Route index element={<Author />} />
      {isAdmin ? (
        <>
          <Route path="new" element={<AuthorUpdate />} />
          <Route path=":id">
            <Route index element={<AuthorDetail />} />
            <Route path="edit" element={<AuthorUpdate />} />
            <Route path="delete" element={<AuthorDeleteDialog />} />
          </Route>
        </>
      ) : undefined}
    </ErrorBoundaryRoutes>
  );
};

export default AuthorRoutes;
