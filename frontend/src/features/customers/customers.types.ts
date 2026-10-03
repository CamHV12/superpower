export type CustomerType = 'INDIVIDUAL' | 'COMPANY';

export interface Customer {
  id: string;
  code: string;
  name: string;
  type: CustomerType;
  email?: string | null;
  phone?: string | null;
  taxCode?: string | null;
  contactPerson?: string | null;
  address?: string | null;
  active: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface CustomerPage {
  content: Customer[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
