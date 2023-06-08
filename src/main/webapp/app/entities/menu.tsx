import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
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
      <MenuItem icon="asterisk" to="/notification">
        Notification
      </MenuItem>
      <MenuItem icon="asterisk" to="/wait-list">
        Wait List
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
