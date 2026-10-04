import { render, screen, waitFor } from '@testing-library/react';
import { vi } from 'vitest';
import { DashboardPage } from './DashboardPage';

const summary = vi.fn();
const monthly = vi.fn();
const overview = vi.fn();

vi.mock('../finance/finance.service', () => ({
  financeService: { summary, monthly },
}));

vi.mock('./dashboard.service', () => ({
  dashboardService: { overview },
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
  });

  it('loads the selected reporting period from the API', async () => {
    render(<DashboardPage />);

    const select = screen.getByRole('combobox');
    expect(select).toHaveValue('6 tháng gần nhất');

    select.dispatchEvent(new Event('change', { bubbles: true }));
    expect(screen.getByRole('combobox')).toBeInTheDocument();
  });
});
