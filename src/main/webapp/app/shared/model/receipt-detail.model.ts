import { type IInventoryReceipt } from '@/shared/model/inventory-receipt.model';
import { type IBook } from '@/shared/model/book.model';

export interface IReceiptDetail {
  id?: number;
  quantity?: number;
  purchasePrice?: number;
  receipt?: IInventoryReceipt | null;
  book?: IBook | null;
}

export class ReceiptDetail implements IReceiptDetail {
  constructor(
    public id?: number,
    public quantity?: number,
    public purchasePrice?: number,
    public receipt?: IInventoryReceipt | null,
    public book?: IBook | null,
  ) {}
}
