import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import { ExpensesPage } from './ExpensesPage';

const listExpenses = vi.fn();
const createExpense = vi.fn();
const deleteExpense = vi.fn();

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

  it('opens expense form', async () => {
    render(<MemoryRouter><ExpensesPage /></MemoryRouter>);

    await waitFor(() => expect(screen.getByText('Văn phòng')).toBeInTheDocument());
    screen.getByRole('button', { name: 'Ghi nhận khoản chi' }).click();

    expect(screen.getByText('Danh mục')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Lưu khoản chi' })).toBeInTheDocument();
  });
});
