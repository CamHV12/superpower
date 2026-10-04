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

export interface PerformanceReport {
  projects: {
    projectId: string;
    code: string;
    name: string;
    customerName: string;
    status: string;
    progress: number;
    budget: number;
    taskCount: number;
    completedTasks: number;
    overdueTasks: number;
    estimatedHours: number;
    actualHours: number;
  }[];
  customers: {
    customerId: string;
    code: string;
    name: string;
    active: boolean;
    projectCount: number;
    activeProjects: number;
    projectBudget: number;
    invoicedAmount: number;
    paidAmount: number;
    receivableAmount: number;
  }[];
}

export async function getOperationalReport() {
  const response = await api.get<OperationalReport>('/reports/operational');
  return response.data;
}

export async function getPerformanceReport() {
  const response = await api.get<PerformanceReport>('/reports/performance');
  return response.data;
}


export async function downloadReport(format: 'excel' | 'pdf', from: string, to: string) {
  const response = await api.get<Blob>('/reports/export/' + format, {
    params: { from, to },
    responseType: 'blob',
  });
  const blob = new Blob([response.data], { type: response.headers['content-type'] });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = format === 'excel' ? 'enterprise-report.xlsx' : 'enterprise-report.pdf';
  document.body.appendChild(link);
  link.click();
  link.remove();
  URL.revokeObjectURL(url);
}
