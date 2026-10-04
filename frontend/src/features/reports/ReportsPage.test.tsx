import { render, screen, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import { ReportsPage } from './ReportsPage';

const summary = vi.hoisted(() => vi.fn());
const operational = vi.hoisted(() => vi.fn());
const performance = vi.hoisted(() => vi.fn());
const downloadReport = vi.hoisted(() => vi.fn());

vi.mock('./reports.service', () => ({
  reportsService: { summary },
  getOperationalReport: operational,
  getPerformanceReport: performance,
  downloadReport,
}));

describe('ReportsPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    summary.mockResolvedValue({
      fromDate: '2026-01-01',
      toDate: '2026-10-04',
      summary: {
        invoicedAmount: 10000000,
        paidAmount: 7000000,
        expenseAmount: 2000000,
        receivableAmount: 3000000,
        netCashFlow: 5000000,
        invoiceCount: 10,
        paidInvoiceCount: 6,
        overdueInvoiceCount: 2,
        projectCount: 8,
        activeProjectCount: 4,
        taskCount: 30,
        completedTaskCount: 20,
        customerCount: 6,
        activeCustomerCount: 5,
      },
      monthly: [{ month: '2026-10', invoicedAmount: 10000000, paidAmount: 7000000, expenseAmount: 2000000, netCashFlow: 5000000 }],
    });
    operational.mockResolvedValue({
      projectStatuses: [{ status: 'ACTIVE', count: 4 }],
      taskStatuses: [{ status: 'DONE', count: 20 }],
      employeePerformance: [{ employeeId: 'e1', employeeName: 'Nguyen Van A', totalTasks: 10, completedTasks: 8, overdueTasks: 1, estimatedHours: 40, actualHours: 35 }],
      customerPerformance: [{ customerId: 'c1', customerName: 'ACME', projects: 2, activeProjects: 1, budget: 100000 }],
    });
    performance.mockResolvedValue({
      projects: [{ projectId: 'p1', code: 'P-001', name: 'Website', customerName: 'ACME', status: 'ACTIVE', progress: 75, budget: 100000, taskCount: 4, completedTasks: 3, overdueTasks: 1, estimatedHours: 40, actualHours: 35 }],
      customers: [{ customerId: 'c1', code: 'C-001', name: 'ACME', active: true, projectCount: 2, activeProjects: 1, projectBudget: 100000, invoicedAmount: 80000, paidAmount: 60000, receivableAmount: 20000 }],
    });
  });

  it('exports the selected report period', async () => {
    const user = (await import('@testing-library/user-event')).default.setup();
    render(<ReportsPage />);
    await waitFor(() => expect(screen.getByText(/Website/)).toBeInTheDocument());
    await user.click(screen.getByRole('button', { name: 'Excel' }));
    await waitFor(() => expect(downloadReport).toHaveBeenCalledWith('excel', expect.any(String), expect.any(String)));
  });

  it('renders financial, project and customer report data', async () => {
    render(<ReportsPage />);

    await waitFor(() => expect(screen.getByText(/Website/)).toBeInTheDocument());
    expect(screen.getByText('Hiệu suất dự án')).toBeInTheDocument();
    expect(screen.getByText('Báo cáo khách hàng')).toBeInTheDocument();
    expect(screen.getByText('ACME')).toBeInTheDocument();
    expect(screen.getAllByText('Dữ liệu thật').length).toBeGreaterThanOrEqual(2);
  });
});
