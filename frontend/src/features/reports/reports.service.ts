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

export interface OperationalReport {
  projectStatuses: { status: string; count: number }[];
  taskStatuses: { status: string; count: number }[];
  employeePerformance: {
    employeeId: string;
    employeeName: string;
    totalTasks: number;
    completedTasks: number;
    overdueTasks: number;
    estimatedHours: number;
    actualHours: number;
  }[];
  customerPerformance: {
    customerId: string;
    customerName: string;
    projects: number;
    activeProjects: number;
    budget: number;
  }[];
}

export async function getOperationalReport() {
  const response = await api.get<OperationalReport>('/reports/operational');
  return response.data;
}
