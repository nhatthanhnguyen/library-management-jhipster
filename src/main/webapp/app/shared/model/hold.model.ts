import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { IBookCopy } from 'app/shared/model/book-copy.model';

export interface IHold {
  id?: number;
  startTime?: string | null;
  endTime?: string | null;
  user?: IUser | null;
  bookCopy?: IBookCopy | null;
}

export const defaultValue: Readonly<IHold> = {};
