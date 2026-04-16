import { type ICategory } from '@/shared/model/category.model';
import { type IAuthor } from '@/shared/model/author.model';

export interface IBook {
  id?: number;
  title?: string;
  isbn?: string | null;
  price?: number;
  stockQuantity?: number;
  imageUrl?: string | null;
  category?: ICategory | null;
  author?: IAuthor | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public title?: string,
    public isbn?: string | null,
    public price?: number,
    public stockQuantity?: number,
    public imageUrl?: string | null,
    public category?: ICategory | null,
    public author?: IAuthor | null,
  ) {}
}
