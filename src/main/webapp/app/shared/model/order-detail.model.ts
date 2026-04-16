import { type IOrder } from '@/shared/model/order.model';
import { type IBook } from '@/shared/model/book.model';

export interface IOrderDetail {
  id?: number;
  quantity?: number;
  salePrice?: number;
  order?: IOrder | null;
  book?: IBook | null;
}

export class OrderDetail implements IOrderDetail {
  constructor(
    public id?: number,
    public quantity?: number,
    public salePrice?: number,
    public order?: IOrder | null,
    public book?: IBook | null,
  ) {}
}
