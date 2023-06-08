import { ICheckout } from 'app/shared/model/checkout.model';
import { IHold } from 'app/shared/model/hold.model';
import { IPublisher } from 'app/shared/model/publisher.model';
import { IBook } from 'app/shared/model/book.model';

export interface IBookCopy {
  id?: number;
  yearPublished?: number;
  checkouts?: ICheckout[] | null;
  holds?: IHold[] | null;
  publisher?: IPublisher | null;
  book?: IBook | null;
}

export const defaultValue: Readonly<IBookCopy> = {};
