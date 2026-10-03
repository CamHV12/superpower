import { api } from '../../services/api';
import type { Customer, CustomerPage, CustomerType } from './customers.types';

const cleanParams = (params: Record<string, unknown>) =>
  Object.fromEntries(Object.entries(params).filter(([, value]) => value !== undefined && value !== ''));

export const customersService = {
  async list(params: { page?: number; size?: number; keyword?: string; active?: boolean } = {}) {
    const response = await api.get<CustomerPage>('/customers', {
      params: cleanParams({ page: 0, size: 10, ...params }),
    });
    return response.data;
  },

  async create(payload: {
    code: string;
    name: string;
    type: CustomerType;
    email?: string;
    phone?: string;
    taxCode?: string;
    contactPerson?: string;
    address?: string;
  }) {
    const response = await api.post<Customer>('/customers', payload);
    return response.data;
  },

  async update(id: string, payload: {
    name: string;
    type: CustomerType;
    email?: string;
    phone?: string;
    taxCode?: string;
    contactPerson?: string;
    address?: string;
    active: boolean;
  }) {
    const response = await api.put<Customer>(`/customers/${id}`, payload);
    return response.data;
  },

  async remove(id: string) {
    await api.delete(`/customers/${id}`);
  },
};
