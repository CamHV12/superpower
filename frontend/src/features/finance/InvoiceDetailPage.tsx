import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { ArrowLeft, CreditCard } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import { financeService } from './finance.service';
import type { Invoice, InvoiceStatus, Payment, PaymentMethod } from './finance.types';

const labels: Record<InvoiceStatus, string> = {
  DRAFT: 'Nháp', SENT: 'Đã gửi', PARTIALLY_PAID: 'Đã thu một phần',
  PAID: 'Đã thanh toán', OVERDUE: 'Quá hạn', CANCELLED: 'Đã hủy',
};
const methods: Record<PaymentMethod, string> = {
  CASH: 'Tiền mặt', BANK_TRANSFER: 'Chuyển khoản', CREDIT_CARD: 'Thẻ', OTHER: 'Khác',
};
const money = (n: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(n);
const today = () => new Date().toISOString().slice(0, 10);

export function InvoiceDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [invoice, setInvoice] = useState<Invoice | null>(null);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showPayment, setShowPayment] = useState(false);
  const [saving, setSaving] = useState(false);
  const [paymentError, setPaymentError] = useState('');
  const [payment, setPayment] = useState({ amount: 0, paymentDate: today(), method: 'BANK_TRANSFER' as PaymentMethod, referenceNumber: '', notes: '' });

  const load = async () => {
    if (!id) return;
    setLoading(true); setError('');
    try {
      const [invoiceData, paymentData] = await Promise.all([financeService.getInvoice(id), financeService.listPayments(id)]);
      setInvoice(invoiceData); setPayments(paymentData);
    } catch { setError('Không thể tải hóa đơn.'); }
    finally { setLoading(false); }
  };

  useEffect(() => { void load(); }, [id]);

  const submitPayment = async (e: FormEvent) => {
    e.preventDefault(); if (!id) return;
    setSaving(true); setPaymentError('');
    try {
      await financeService.createPayment({
        invoiceId: id, amount: payment.amount, paymentDate: payment.paymentDate,
        method: payment.method, referenceNumber: payment.referenceNumber || undefined,
        notes: payment.notes || undefined,
      });
      setShowPayment(false);
      setPayment({ amount: 0, paymentDate: today(), method: 'BANK_TRANSFER', referenceNumber: '', notes: '' });
      await load();
    } catch { setPaymentError('Không thể ghi nhận thanh toán. Số tiền có thể vượt quá công nợ còn lại.'); }
    finally { setSaving(false); }
  };

  if (loading) return <Card><p className="text-sm text-slate-500">Đang tải hóa đơn...</p></Card>;
  if (error || !invoice) return <EmptyState title="Không tìm thấy hóa đơn" description={error || 'Hóa đơn không tồn tại.'} />;

  const variant = invoice.status === 'PAID' ? 'success' : invoice.status === 'OVERDUE' || invoice.status === 'CANCELLED' ? 'danger' : 'warning';

  return (
    <div className="space-y-6">
      <Link to="/finance" className="inline-flex items-center gap-2 text-sm font-medium text-slate-500 hover:text-blue-600"><ArrowLeft className="size-4" /> Quay lại hóa đơn</Link>
      <Card><div className="flex flex-col justify-between gap-4 md:flex-row md:items-start">
        <div><p className="text-xs font-semibold text-slate-500">{invoice.invoiceNumber}</p><h1 className="mt-2 text-2xl font-bold">{invoice.customerName}</h1><p className="mt-1 text-sm text-slate-500">Phát hành {invoice.issueDate} · Đến hạn {invoice.dueDate}</p></div>
        <div className="flex items-center gap-3"><Badge variant={variant}>{labels[invoice.status]}</Badge><Button onClick={() => setShowPayment(v => !v)} disabled={invoice.remainingAmount <= 0 || invoice.status === 'CANCELLED'}><CreditCard className="size-4" />Ghi nhận thanh toán</Button></div>
      </div></Card>

      <div className="grid gap-4 md:grid-cols-3">
        <Card><p className="text-sm text-slate-500">Tổng hóa đơn</p><p className="mt-2 text-xl font-bold">{money(invoice.totalAmount)}</p></Card>
        <Card><p className="text-sm text-slate-500">Đã thanh toán</p><p className="mt-2 text-xl font-bold">{money(invoice.paidAmount)}</p></Card>
        <Card><p className="text-sm text-slate-500">Còn phải thu</p><p className="mt-2 text-xl font-bold">{money(invoice.remainingAmount)}</p></Card>
      </div>

      {showPayment && <Card><form onSubmit={submitPayment} className="grid gap-4 md:grid-cols-2">
        <div><label className="text-sm font-medium">Số tiền</label><input required min="0.01" max={invoice.remainingAmount} step="1000" type="number" value={payment.amount} onChange={e => setPayment({ ...payment, amount: Number(e.target.value) })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <div><label className="text-sm font-medium">Ngày thanh toán</label><input required type="date" value={payment.paymentDate} onChange={e => setPayment({ ...payment, paymentDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <div><label className="text-sm font-medium">Phương thức</label><select value={payment.method} onChange={e => setPayment({ ...payment, method: e.target.value as PaymentMethod })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">{Object.entries(methods).map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></div>
        <div><label className="text-sm font-medium">Mã tham chiếu</label><input value={payment.referenceNumber} onChange={e => setPayment({ ...payment, referenceNumber: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <textarea value={payment.notes} onChange={e => setPayment({ ...payment, notes: e.target.value })} placeholder="Ghi chú" rows={2} className="md:col-span-2 rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
        {paymentError && <p className="md:col-span-2 text-sm text-red-600">{paymentError}</p>}
        <div className="md:col-span-2 flex justify-end"><Button type="submit" loading={saving}>Xác nhận thanh toán</Button></div>
      </form></Card>}

      <Card><h2 className="font-semibold">Chi tiết hóa đơn</h2>
        <div className="mt-4 overflow-x-auto"><table className="w-full min-w-[650px] text-left text-sm">
          <thead className="bg-slate-50 dark:bg-slate-900"><tr><th className="px-4 py-3">Mô tả</th><th className="px-4 py-3">SL</th><th className="px-4 py-3">Đơn giá</th><th className="px-4 py-3">Thành tiền</th></tr></thead>
          <tbody className="divide-y divide-slate-100 dark:divide-slate-800">{invoice.items.map(item => <tr key={item.id}><td className="px-4 py-3">{item.description}</td><td className="px-4 py-3">{item.quantity}</td><td className="px-4 py-3">{money(item.unitPrice)}</td><td className="px-4 py-3 font-medium">{money(item.amount)}</td></tr>)}</tbody>
        </table></div>
        <div className="mt-5 ml-auto max-w-sm space-y-2 border-t border-slate-100 pt-4 text-sm dark:border-slate-800">
          <div className="flex justify-between"><span>Tạm tính</span><span>{money(invoice.subtotal)}</span></div>
          <div className="flex justify-between"><span>Thuế</span><span>{money(invoice.taxAmount)}</span></div>
          <div className="flex justify-between"><span>Chiết khấu</span><span>-{money(invoice.discountAmount)}</span></div>
          <div className="flex justify-between text-base font-bold"><span>Tổng cộng</span><span>{money(invoice.totalAmount)}</span></div>
        </div>
      </Card>

      <Card><div className="flex items-center justify-between"><h2 className="font-semibold">Lịch sử thanh toán</h2><span className="text-xs text-slate-500">{payments.length} giao dịch</span></div>
        {payments.length === 0 ? <p className="py-6 text-sm text-slate-500">Chưa có giao dịch thanh toán.</p> :
        <div className="mt-4 overflow-x-auto"><table className="w-full min-w-[700px] text-left text-sm"><thead className="bg-slate-50 dark:bg-slate-900"><tr><th className="px-4 py-3">Ngày</th><th className="px-4 py-3">Số tiền</th><th className="px-4 py-3">Phương thức</th><th className="px-4 py-3">Tham chiếu</th><th className="px-4 py-3">Ghi chú</th></tr></thead><tbody className="divide-y divide-slate-100 dark:divide-slate-800">{payments.map(p => <tr key={p.id}><td className="px-4 py-3">{p.paymentDate}</td><td className="px-4 py-3 font-medium">{money(p.amount)}</td><td className="px-4 py-3">{methods[p.method]}</td><td className="px-4 py-3">{p.referenceNumber || '—'}</td><td className="px-4 py-3">{p.notes || '—'}</td></tr>)}</tbody></table></div>}
      </Card>
    </div>
  );
}
