import { getUrlParameter } from 'react-jhipster';

export interface IPaginationSearchState {
  itemsPerPage: number;
  sort: string;
  order: string;
  activePage: number;
  query: string;
}

export const getSortSearchState = (
  location: { search: string },
  itemsPerPage: number,
  sortField = 'id',
  sortOrder = 'asc',
  query = ''
): IPaginationSearchState => {
  const pageParam = getUrlParameter('page', location.search);
  const sortParam = getUrlParameter('sort', location.search);
  const searchTextParam = getUrlParameter('q', location.search);
  let sort = sortField;
  let order = sortOrder;
  let activePage = 1;
  let q = '';
  if (pageParam !== '' && !isNaN(parseInt(pageParam, 10))) {
    activePage = parseInt(pageParam, 10);
  }
  if (sortParam !== '') {
    sort = sortParam.split(',')[0];
    order = sortParam.split(',')[1];
  }
  if (searchTextParam == null) q = query;
  return { itemsPerPage, sort, order, activePage, query: q };
};
