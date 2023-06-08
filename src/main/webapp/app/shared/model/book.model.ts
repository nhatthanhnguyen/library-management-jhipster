import { IBookCopy } from 'app/shared/model/book-copy.model';
import { IWaitList } from 'app/shared/model/wait-list.model';
import { ICategory } from 'app/shared/model/category.model';
import { IAuthor } from 'app/shared/model/author.model';

export interface IBook {
  id?: number;
  title?: string;
  bookCopies?: IBookCopy[] | null;
  waitLists?: IWaitList[] | null;
  category?: ICategory | null;
  authors?: IAuthor[] | null;
}

export const defaultValue: Readonly<IBook> = {};
