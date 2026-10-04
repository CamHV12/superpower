import { api } from '../../services/api';

export interface ReportSummary {
  invoicedAmount: number;
  paidAmount: number;
  expenseAmount: number;
  receivableAmount: number;
  netCashFlow: number;
  invoiceCount: number;
  paidInvoiceCount: number;
  overdueInvoiceCount: number;
  projectCount: number;
  activeProjectCount: number;
  taskCount: number;
  completedTaskCount: number;
  customerCount: number;
  activeCustomerCount: number;
}

export interface ReportMonthlyPoint {
  month: string;
  invoicedAmount: number;
  paidAmount: number;
  expenseAmount: number;
  netCashFlow: number;
}

export interface ReportResponse {
  fromDate: string;
  toDate: string;
  summary: ReportSummary;
  monthly: ReportMonthlyPoint[];
}

export const reportsService = {
  async summary(from: string, to: string) {
    const response = await api.get<ReportResponse>('/reports/summary', {
      params: { from, to },
    });
    return response.data;
  },
};
