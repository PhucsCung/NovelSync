export interface IAuthor {
  id?: number;
  authorName?: string;
}

export class Author implements IAuthor {
  constructor(
    public id?: number,
    public authorName?: string,
  ) {}
}
