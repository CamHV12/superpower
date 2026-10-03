import { api } from '../../services/api';
import type { CustomerOption, Invoice, InvoicePage, Payment, PaymentMethod } from './finance.types';

const cleanParams = (params: Record<string, unknown>) =>
  Object.fromEntries(Object.entries(params).filter(([, value]) => value !== undefined && value !== ''));

export const financeService = {
  async listInvoices(params: { page?: number; size?: number } = {}) {
    const response = await api.get<InvoicePage>('/invoices', {
      params: cleanParams({ page: 0, size: 10, ...params }),
    });
    return response.data;
  },

  async getInvoice(id: string) {
    const response = await api.get<Invoice>('/invoices/' + id);
    return response.data;
  },

  async createInvoice(payload: {
    invoiceNumber: string;
    customerId: string;
    projectId?: string;
    issueDate: string;
    dueDate: string;
    taxAmount: number;
    discountAmount: number;
    notes?: string;
    items: { description: string; quantity: number; unitPrice: number }[];
  }) {
    const response = await api.post<Invoice>('/invoices', payload);
    return response.data;
  },

  async listPayments(invoiceId: string) {
    const response = await api.get<Payment[]>('/payments', { params: { invoiceId } });
    return response.data;
  },

  async createPayment(payload: {
    invoiceId: string;
    amount: number;
    paymentDate: string;
    method: PaymentMethod;
    referenceNumber?: string;
    notes?: string;
  }) {
    const response = await api.post<Payment>('/payments', payload);
    return response.data;
  },

  async listCustomers(size = 100) {
    const response = await api.get<{ content: CustomerOption[] }>('/customers', {
      params: { page: 0, size, active: true },
    });
    return response.data;
  },
};
