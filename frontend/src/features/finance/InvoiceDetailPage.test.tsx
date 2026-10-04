import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import { InvoiceDetailPage } from './InvoiceDetailPage';

const getInvoice = vi.hoisted(() => vi.fn());
const listPayments = vi.hoisted(() => vi.fn());
const createPayment = vi.hoisted(() => vi.fn());

vi.mock('./finance.service', () => ({
  financeService: { getInvoice, listPayments, createPayment },
}));

const invoice = {
  id: 'i1',
  invoiceNumber: 'INV-001',
  customerId: 'c1',
  customerName: 'Công ty ABC',
  projectId: null,
  projectName: null,
  issueDate: '2026-10-03',
  dueDate: '2026-10-31',
  status: 'SENT',
  subtotal: 10000000,
  taxAmount: 1000000,
  discountAmount: 0,
  totalAmount: 11000000,
  paidAmount: 5000000,
  remainingAmount: 6000000,
  notes: null,
  items: [{
    id: 'item-1',
    description: 'Backend development',
    quantity: 1,
    unitPrice: 10000000,
    amount: 10000000,
  }],
};

describe('InvoiceDetailPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    getInvoice.mockResolvedValue(invoice);
    listPayments.mockResolvedValue([]);
    createPayment.mockResolvedValue({
      id: 'p1',
      invoiceId: 'i1',
      invoiceNumber: 'INV-001',
      amount: 6000000,
      paymentDate: '2026-10-03',
      method: 'BANK_TRANSFER',
      referenceNumber: 'REF-001',
      notes: null,
      createdAt: '2026-10-03T10:00:00Z',
    });
  });

  const renderPage = () => render(
    <MemoryRouter initialEntries={['/finance/invoices/i1']}>
      <Routes>
        <Route path="/finance/invoices/:id" element={<InvoiceDetailPage />} />
      </Routes>
    </MemoryRouter>
  );

  it('loads invoice and payment history', async () => {
    renderPage();

    await waitFor(() => expect(screen.getByText('INV-001')).toBeInTheDocument());
    expect(screen.getByText('Đã thanh toán')).toBeInTheDocument();
    expect(screen.getByText(/6\.000\.000/)).toBeInTheDocument();
    expect(listPayments).toHaveBeenCalledWith('i1');
  });

  it('opens payment form and submits payment', async () => {
    renderPage();

    await waitFor(() => expect(screen.getByText('INV-001')).toBeInTheDocument());
    const user = userEvent.setup();
    await user.click(screen.getByRole('button', { name: 'Ghi nhận thanh toán' }));

    await waitFor(() => expect(screen.getByRole('button', { name: 'Xác nhận thanh toán' })).toBeInTheDocument());

    const amount = screen.getByLabelText('Số tiền');
    await user.clear(amount);
    await user.type(amount, '6000000');
    expect(amount).toHaveValue(6000000);
    await user.click(screen.getByRole('button', { name: 'Xác nhận thanh toán' }));

    await waitFor(() => expect(createPayment).toHaveBeenCalledWith(expect.objectContaining({
      invoiceId: 'i1',
      amount: 6000000,
      method: 'BANK_TRANSFER',
    })));
  });
});
