import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { Type } from 'app/shared/model/enumerations/type.model';

export interface INotification {
  id?: number;
  sentAt?: string | null;
  type?: Type | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<INotification> = {};
