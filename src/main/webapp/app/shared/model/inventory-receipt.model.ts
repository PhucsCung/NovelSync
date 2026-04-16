import { type IUser } from '@/shared/model/user.model';

export interface IInventoryReceipt {
  id?: number;
  receiptDate?: Date;
  totalAmount?: number;
  user?: IUser | null;
}

export class InventoryReceipt implements IInventoryReceipt {
  constructor(
    public id?: number,
    public receiptDate?: Date,
    public totalAmount?: number,
    public user?: IUser | null,
  ) {}
}
