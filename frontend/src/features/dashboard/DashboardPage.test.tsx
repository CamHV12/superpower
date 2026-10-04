import { render, screen, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import { DashboardPage } from './DashboardPage';

const summary = vi.hoisted(() => vi.fn());
const monthly = vi.hoisted(() => vi.fn());
const overview = vi.hoisted(() => vi.fn());
const operational = vi.hoisted(() => vi.fn());

vi.mock('../finance/finance.service', () => ({
  financeService: { summary, monthly },
}));

vi.mock('./dashboard.service', () => ({
  dashboardService: { overview, operational },
}));

describe('DashboardPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    summary.mockResolvedValue({
      totalInvoiced: 3000000,
      totalPaid: 1500000,
      totalReceivable: 1500000,
      totalExpense: 400000,
      netCashFlow: 1100000,
      overdueInvoices: 1,
      overdueAmount: 1500000,
    });
    monthly.mockResolvedValue([{
      month: '2026-10',
      paidAmount: 1500000,
      expenseAmount: 400000,
      netCashFlow: 1100000,
    }]);
    operational.mockResolvedValue({
      projectStatuses: [{ status: 'ACTIVE', count: 5 }],
      taskStatuses: [{ status: 'TODO', count: 10 }],
      employeeWorkloads: [{ employeeId: 'e1', employeeName: 'Nguyen Van A', openTasks: 3, overdueTasks: 1, estimatedHours: 20, actualHours: 15 }],
      customerKpis: [{ customerId: 'c1', customerName: 'ABC Company', projectCount: 2, activeProjects: 1, projectBudget: 100000000 }],
      recentActivities: [{ id: 'p1', type: 'PROJECT', title: 'Dự án mới', description: 'Project Alpha', occurredAt: '2026-10-04T04:00:00Z' }],
    });
    overview.mockResolvedValue({
      totalEmployees: 20,
      activeEmployees: 17,
      totalProjects: 12,
      activeProjects: 5,
      totalCustomers: 30,
      activeCustomers: 26,
      totalTasks: 80,
      overdueTasks: 7,
    });
  });

  it('renders real operational and finance KPIs', async () => {
    render(<DashboardPage />);

    expect(screen.getByRole('heading', { name: 'Tổng quan doanh nghiệp' })).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText('17 / 20')).toBeInTheDocument());
    expect(screen.getByText('5 / 12')).toBeInTheDocument();
    expect(screen.getByText('26 / 30')).toBeInTheDocument();
    expect(screen.getByText('7 / 80')).toBeInTheDocument();
    expect(screen.getByText('Project Alpha')).toBeInTheDocument();
    expect(screen.getByText('Nguyen Van A')).toBeInTheDocument();
  });

  it('loads the selected reporting period from the API', async () => {
    render(<DashboardPage />);

    const select = screen.getByRole('combobox');
    expect(select).toHaveValue('6 tháng gần nhất');

    select.dispatchEvent(new Event('change', { bubbles: true }));
    expect(screen.getByRole('combobox')).toBeInTheDocument();
  });
});
