import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getSortState, JhiItemCount, JhiPagination, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, AUTHORITIES } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getEntities } from './hold.reducer';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { IHold } from 'app/shared/model/hold.model';

export const Hold = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getSortState(location, ITEMS_PER_PAGE, 'startTime', 'desc'), location.search)
  );

  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const holdList = useAppSelector(state => state.hold.entities);
  const loading = useAppSelector(state => state.hold.loading);
  const totalItems = useAppSelector(state => state.hold.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      })
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(location.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [location.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  return (
    <div>
      <h2 id="hold-heading" data-cy="HoldHeading">
        Holds
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          {/*{isAdmin ? (*/}
          {/*  <Link to="/hold/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">*/}
          {/*    <FontAwesomeIcon icon="plus" />*/}
          {/*    &nbsp; Create a new Hold*/}
          {/*  </Link>*/}
          {/*) : undefined}*/}
        </div>
      </h2>
      <div className="table-responsive">
        {holdList && holdList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('startTime')}>
                  Start Time <FontAwesomeIcon icon="sort" />
                </th>
                <th className="hand" onClick={sort('endTime')}>
                  End Time <FontAwesomeIcon icon="sort" />
                </th>
                {isAdmin ? (
                  <th>
                    User <FontAwesomeIcon icon="sort" />
                  </th>
                ) : undefined}
                <th>
                  Book Copy <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {holdList.map((hold: IHold, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/hold/${hold.id}`} color="link" size="sm">
                      {hold.id}
                    </Button>
                  </td>
                  <td>{hold.startTime ? <TextFormat type="date" value={hold.startTime} format={APP_DATE_FORMAT} /> : null}</td>
                  <td>{hold.endTime ? <TextFormat type="date" value={hold.endTime} format={APP_DATE_FORMAT} /> : null}</td>
                  {isAdmin ? <td>{hold.user ? hold.user.login : ''}</td> : undefined}
                  <td>{hold.bookCopy ? <Link to={`/book-copy/${hold.bookCopy.id}`}>{hold.bookCopy.id}</Link> : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      {isAdmin ? (
                        <>
                          {/*<Button tag={Link} to={`/hold/${hold.id}`} color="info" size="sm" data-cy="entityDetailsButton">*/}
                          {/*  <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>*/}
                          {/*</Button>*/}
                          {hold.endTime == null ? (
                            <Button
                              tag={Link}
                              to={`/hold/${hold.id}/issue?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                              color="primary"
                              size="sm"
                              data-cy="entityEditButton"
                            >
                              <FontAwesomeIcon icon="plus" /> <span className="d-none d-md-inline">Issue</span>
                            </Button>
                          ) : undefined}
                        </>
                      ) : undefined}
                      <Button
                        tag={Link}
                        to={`/hold/${hold.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Holds found</div>
        )}
      </div>
      {totalItems ? (
        <div className={holdList && holdList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Hold;
