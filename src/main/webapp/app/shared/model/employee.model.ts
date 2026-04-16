import { type IUser } from '@/shared/model/user.model';

export interface IEmployee {
  id?: number;
  fullName?: string;
  hireDate?: Date;
  salary?: number;
  user?: IUser | null;
}

export class Employee implements IEmployee {
  constructor(
    public id?: number,
    public fullName?: string,
    public hireDate?: Date,
    public salary?: number,
    public user?: IUser | null,
  ) {}
}
