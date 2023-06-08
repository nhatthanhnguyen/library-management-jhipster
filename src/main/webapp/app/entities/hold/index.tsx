import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import Hold from './hold';
import HoldDetail from './hold-detail';
import HoldUpdate from './hold-update';
import HoldDeleteDialog from './hold-delete-dialog';

const HoldRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<Hold />} />
    <Route path="new" element={<HoldUpdate />} />
    <Route path=":id">
      <Route index element={<HoldDetail />} />
      <Route path="edit" element={<HoldUpdate />} />
      <Route path="delete" element={<HoldDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default HoldRoutes;
