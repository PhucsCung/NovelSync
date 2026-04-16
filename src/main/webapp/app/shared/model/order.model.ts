import { type ICustomer } from '@/shared/model/customer.model';
import { type IUser } from '@/shared/model/user.model';

import { type OrderStatus } from '@/shared/model/enumerations/order-status.model';
export interface IOrder {
  id?: number;
  orderDate?: Date;
  totalAmount?: number;
  status?: keyof typeof OrderStatus | null;
  customer?: ICustomer | null;
  user?: IUser | null;
}

export class Order implements IOrder {
  constructor(
    public id?: number,
    public orderDate?: Date,
    public totalAmount?: number,
    public status?: keyof typeof OrderStatus | null,
    public customer?: ICustomer | null,
    public user?: IUser | null,
  ) {}
}
