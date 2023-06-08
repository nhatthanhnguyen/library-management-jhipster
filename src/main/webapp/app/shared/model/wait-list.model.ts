import { IUser } from 'app/shared/model/user.model';
import { IBook } from 'app/shared/model/book.model';

export interface IWaitList {
  id?: number;
  user?: IUser | null;
  book?: IBook | null;
}

export const defaultValue: Readonly<IWaitList> = {};
