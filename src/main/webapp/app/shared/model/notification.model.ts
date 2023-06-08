import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';

export interface INotification {
  id?: number;
  sentAt?: string | null;
  type?: string | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<INotification> = {};
