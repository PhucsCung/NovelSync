export interface ICustomer {
  id?: number;
  fullName?: string;
  phoneNumber?: string | null;
}

export class Customer implements ICustomer {
  constructor(
    public id?: number,
    public fullName?: string,
    public phoneNumber?: string | null,
  ) {}
}
