import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';
import { ExpensesPage } from './ExpensesPage';

const listExpenses = vi.hoisted(() => vi.fn());
const createExpense = vi.hoisted(() => vi.fn());
const deleteExpense = vi.hoisted(() => vi.fn());

vi.mock('./finance.service', () => ({
  financeService: { listExpenses, createExpense, deleteExpense },
}));

describe('ExpensesPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    listExpenses.mockResolvedValue({
      content: [{
        id: 'e1',
        category: 'Văn phòng',
        amount: 2500000,
        expenseDate: '2026-10-03',
        vendor: 'Nhà cung cấp A',
        paymentMethod: 'BANK_TRANSFER',
        notes: null,
        status: 'RECORDED',
      }],
      totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true,
    });
  });

  it('loads expenses from API', async () => {
    render(<MemoryRouter><ExpensesPage /></MemoryRouter>);

    await waitFor(() => expect(screen.getByText('Văn phòng')).toBeInTheDocument());
    expect(screen.getByText('Nhà cung cấp A')).toBeInTheDocument();
    expect(listExpenses).toHaveBeenCalled();
  });

  it('applies expense filters through the API', async () => {
    render(<MemoryRouter><ExpensesPage /></MemoryRouter>);

    await waitFor(() => expect(screen.getByText('Văn phòng')).toBeInTheDocument());

    const search = screen.getByPlaceholderText('Danh mục, nhà cung cấp, ghi chú...');
    await import('@testing-library/user-event').then(({ default: userEvent }) => userEvent.type(search, 'office'));
    screen.getByRole('button', { name: 'Lọc' }).click();

    await waitFor(() => {
      expect(listExpenses).toHaveBeenLastCalledWith(expect.objectContaining({
        page: 0,
        size: 10,
        keyword: 'office',
      }));
    });
  });

  it('opens expense form', async () => {
    render(<MemoryRouter><ExpensesPage /></MemoryRouter>);

    await waitFor(() => expect(screen.getByText('Văn phòng')).toBeInTheDocument());
    await userEvent.click(screen.getByRole('button', { name: 'Ghi nhận khoản chi' }));

    expect(screen.getAllByText('Danh mục').length).toBeGreaterThanOrEqual(2);
    expect(screen.getByRole('button', { name: 'Lưu khoản chi' })).toBeInTheDocument();
  });
});
