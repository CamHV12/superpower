import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import userEvent from '@testing-library/user-event';
import { InvoicesPage } from './InvoicesPage';

const listInvoices = vi.hoisted(() => vi.fn());
const listCustomers = vi.hoisted(() => vi.fn());
const createInvoice = vi.hoisted(() => vi.fn());

vi.mock('./finance.service', () => ({
  financeService: { listInvoices, listCustomers, createInvoice },
}));

describe('InvoicesPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    listInvoices.mockResolvedValue({
      content: [{
        id: 'i1',
        invoiceNumber: 'INV-001',
        customerId: 'c1',
        customerName: 'Công ty ABC',
        projectId: null,
        projectName: null,
        issueDate: '2026-10-03',
        dueDate: '2026-10-31',
        status: 'PARTIALLY_PAID',
        subtotal: 10000000,
        taxAmount: 1000000,
        discountAmount: 0,
        totalAmount: 11000000,
        paidAmount: 5000000,
        remainingAmount: 6000000,
        items: [],
      }],
      totalElements: 1, totalPages: 1, size: 10, number: 0, first: true, last: true,
    });
    listCustomers.mockResolvedValue({ content: [{ id: 'c1', code: 'CUS-001', name: 'Công ty ABC' }] });
  });

  it('loads invoice payment summary from API data', async () => {
    render(<MemoryRouter><InvoicesPage /></MemoryRouter>);
    await waitFor(() => expect(screen.getByText('INV-001')).toBeInTheDocument());
    expect(screen.getByText('Công ty ABC')).toBeInTheDocument();
    expect(listInvoices).toHaveBeenCalled();
  });

  it('opens invoice creation form', async () => {
    render(<MemoryRouter><InvoicesPage /></MemoryRouter>);
    await waitFor(() => expect(screen.getByText('INV-001')).toBeInTheDocument());
    await userEvent.click(screen.getByRole('button', { name: 'Tạo hóa đơn' }));
    expect(screen.getByPlaceholderText('INV-001')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Lưu hóa đơn' })).toBeInTheDocument();
  });
  it('adds another invoice item row', async () => {
    render(<MemoryRouter><InvoicesPage /></MemoryRouter>);
    await waitFor(() => expect(screen.getByText('INV-001')).toBeInTheDocument());

    screen.getByRole('button', { name: 'Tạo hóa đơn' }).click();
    expect(screen.getAllByPlaceholderText('Dịch vụ phát triển phần mềm')).toHaveLength(1);

    screen.getByRole('button', { name: 'Thêm dòng' }).click();
    expect(screen.getAllByPlaceholderText('Dịch vụ phát triển phần mềm')).toHaveLength(2);
    expect(screen.getAllByRole('button', { name: 'Xóa' })).toHaveLength(2);
  });

});
