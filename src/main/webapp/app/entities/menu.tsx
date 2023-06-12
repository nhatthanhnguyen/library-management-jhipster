import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

const EntitiesMenu = () => {
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/book">
        Book
      </MenuItem>
      <MenuItem icon="asterisk" to="/author">
        Author
      </MenuItem>
      <MenuItem icon="asterisk" to="/category">
        Category
      </MenuItem>
      <MenuItem icon="asterisk" to="/publisher">
        Publisher
      </MenuItem>
      <MenuItem icon="asterisk" to="/book-copy">
        Book Copy
      </MenuItem>
      <MenuItem icon="asterisk" to="/checkout">
        Checkout
      </MenuItem>
      <MenuItem icon="asterisk" to="/hold">
        Hold
      </MenuItem>
      {isAdmin ? (
        <>
          <MenuItem icon="asterisk" to="/notification">
            Notification
          </MenuItem>
          <MenuItem icon="asterisk" to="/wait-list">
            Wait List
          </MenuItem>
        </>
      ) : undefined}
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
