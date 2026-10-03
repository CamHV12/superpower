import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { Eye, Plus } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import { financeService } from './finance.service';
import type { CustomerOption, Invoice, InvoiceStatus } from './finance.types';

const labels: Record<InvoiceStatus, string> = {
  DRAFT: 'Nháp', SENT: 'Đã gửi', PARTIALLY_PAID: 'Đã thu một phần',
  PAID: 'Đã thanh toán', OVERDUE: 'Quá hạn', CANCELLED: 'Đã hủy',
};
const variants: Record<InvoiceStatus, 'neutral' | 'success' | 'warning' | 'danger'> = {
  DRAFT: 'neutral', SENT: 'warning', PARTIALLY_PAID: 'warning',
  PAID: 'success', OVERDUE: 'danger', CANCELLED: 'danger',
};
const money = (n: number) => new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(n);
const today = () => new Date().toISOString().slice(0, 10);

export function InvoicesPage() {
  const [items, setItems] = useState<Invoice[]>([]);
  const [customers, setCustomers] = useState<CustomerOption[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState({ keyword: '', status: '' as InvoiceStatus | '', customerId: '', fromDate: '', toDate: '' });
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');
  const [form, setForm] = useState({
    invoiceNumber: '', customerId: '', issueDate: today(), dueDate: today(),
    taxAmount: 0, discountAmount: 0, notes: '',
    items: [{ description: '', quantity: 1, unitPrice: 0 }],
  });

  const load = async () => {
    setLoading(true); setError('');
    try {
      const result = await financeService.listInvoices({ page, size: 10, ...filters });
      setItems(result.content); setTotalPages(result.totalPages);
    } catch { setError('Không thể tải danh sách hóa đơn.'); }
    finally { setLoading(false); }
  };

  useEffect(() => {
    void load();
  }, [page, filters.keyword, filters.status, filters.customerId, filters.fromDate, filters.toDate]);

  useEffect(() => {
    financeService.listCustomers().then(r => setCustomers(r.content)).catch(() => setCustomers([]));
  }, []);

  const updateFilter = (key: keyof typeof filters, value: string) => {
    setPage(0);
    setFilters(current => ({ ...current, [key]: value }));
  };

  const resetFilters = () => {
    setPage(0);
    setFilters({ keyword: '', status: '', customerId: '', fromDate: '', toDate: '' });
  };

  const submit = async (e: FormEvent) => {
    e.preventDefault(); setSaving(true); setFormError('');
    try {
      await financeService.createInvoice({
        invoiceNumber: form.invoiceNumber, customerId: form.customerId,
        issueDate: form.issueDate, dueDate: form.dueDate,
        taxAmount: form.taxAmount, discountAmount: form.discountAmount,
        notes: form.notes || undefined, items: form.items,
      });
      setShowForm(false);
      setForm({ invoiceNumber: '', customerId: '', issueDate: today(), dueDate: today(), taxAmount: 0, discountAmount: 0, notes: '', items: [{ description: '', quantity: 1, unitPrice: 0 }] });
      await load();
    } catch { setFormError('Không thể tạo hóa đơn. Kiểm tra dữ liệu và số hóa đơn.'); }
    finally { setSaving(false); }
  };

  const subtotal = form.items.reduce((sum, item) => sum + item.quantity * item.unitPrice, 0);
  const total = subtotal + form.taxAmount - form.discountAmount;

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div><h1 className="text-2xl font-bold tracking-tight">Tài chính · Hóa đơn</h1><p className="mt-1 text-sm text-slate-500">Quản lý hóa đơn và công nợ khách hàng.</p></div>
        <Button onClick={() => setShowForm(v => !v)}><Plus className="size-4" />{showForm ? 'Đóng' : 'Tạo hóa đơn'}</Button>
      </div>

      <Card>
        <div className="grid gap-3 md:grid-cols-2 lg:grid-cols-5">
          <input
            value={filters.keyword}
            onChange={e => updateFilter('keyword', e.target.value)}
            placeholder="Tìm số hóa đơn, khách hàng..."
            className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"
          />
          <select value={filters.status} onChange={e => updateFilter('status', e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Tất cả trạng thái</option>
            {Object.entries(labels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
          <select value={filters.customerId} onChange={e => updateFilter('customerId', e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Tất cả khách hàng</option>
            {customers.map(c => <option key={c.id} value={c.id}>{c.code} · {c.name}</option>)}
          </select>
          <input type="date" value={filters.fromDate} onChange={e => updateFilter('fromDate', e.target.value)} title="Từ ngày phát hành" className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
          <div className="flex gap-2">
            <input type="date" value={filters.toDate} onChange={e => updateFilter('toDate', e.target.value)} title="Đến ngày phát hành" className="min-w-0 flex-1 rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            <Button type="button" variant="secondary" onClick={resetFilters}>Xóa</Button>
          </div>
        </div>
      </Card>

      {showForm && <Card><form onSubmit={submit} className="grid gap-4 md:grid-cols-2">
        <div><label className="text-sm font-medium">Số hóa đơn</label><input required value={form.invoiceNumber} onChange={e => setForm({ ...form, invoiceNumber: e.target.value })} placeholder="INV-001" className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <div><label className="text-sm font-medium">Khách hàng</label><select required value={form.customerId} onChange={e => setForm({ ...form, customerId: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"><option value="">Chọn khách hàng</option>{customers.map(c => <option key={c.id} value={c.id}>{c.code} · {c.name}</option>)}</select></div>
        <div><label className="text-sm font-medium">Ngày phát hành</label><input required type="date" value={form.issueDate} onChange={e => setForm({ ...form, issueDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <div><label className="text-sm font-medium">Ngày đến hạn</label><input required type="date" value={form.dueDate} onChange={e => setForm({ ...form, dueDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <div className="md:col-span-2">
          <div className="mb-2 flex items-center justify-between">
            <label className="text-sm font-medium">Chi tiết hóa đơn</label>
            <Button type="button" variant="secondary" onClick={() => setForm(current => ({
              ...current,
              items: [...current.items, { description: '', quantity: 1, unitPrice: 0 }],
            }))}><Plus className="size-4" />Thêm dòng</Button>
          </div>
          <div className="space-y-3">
            {form.items.map((item, index) => (
              <div key={index} className="grid gap-3 rounded-xl border border-slate-200 p-3 dark:border-slate-700 md:grid-cols-[1fr_140px_180px_auto]">
                <input required value={item.description} onChange={e => setForm(current => ({
                  ...current,
                  items: current.items.map((row, i) => i === index ? { ...row, description: e.target.value } : row),
                }))} placeholder="Dịch vụ phát triển phần mềm" className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
                <input required min="0.0001" step="0.0001" type="number" value={item.quantity} onChange={e => setForm(current => ({
                  ...current,
                  items: current.items.map((row, i) => i === index ? { ...row, quantity: Number(e.target.value) } : row),
                }))} aria-label={`Số lượng dòng ${index + 1}`} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
                <input required min="0" step="1000" type="number" value={item.unitPrice} onChange={e => setForm(current => ({
                  ...current,
                  items: current.items.map((row, i) => i === index ? { ...row, unitPrice: Number(e.target.value) } : row),
                }))} aria-label={`Đơn giá dòng ${index + 1}`} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
                <Button type="button" variant="ghost" disabled={form.items.length === 1} onClick={() => setForm(current => ({
                  ...current,
                  items: current.items.filter((_, i) => i !== index),
                }))}>Xóa</Button>
              </div>
            ))}
          </div>
        </div>
        <div><label className="text-sm font-medium">Thuế</label><input min="0" type="number" value={form.taxAmount} onChange={e => setForm({ ...form, taxAmount: Number(e.target.value) })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <div><label className="text-sm font-medium">Chiết khấu</label><input min="0" type="number" value={form.discountAmount} onChange={e => setForm({ ...form, discountAmount: Number(e.target.value) })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
        <div className="md:col-span-2 rounded-xl bg-slate-50 p-3 dark:bg-slate-900"><span className="text-xs text-slate-500">Tổng dự kiến</span><p className="font-bold">{money(Math.max(total, 0))}</p></div>
        <textarea value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })} placeholder="Ghi chú" rows={2} className="md:col-span-2 rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
        {formError && <p className="md:col-span-2 text-sm text-red-600">{formError}</p>}
        <div className="md:col-span-2 flex justify-end"><Button type="submit" loading={saving}>Lưu hóa đơn</Button></div>
      </form></Card>}

      {error ? <Card><p className="text-sm text-red-600">{error}</p></Card> :
       loading ? <Card><p className="text-sm text-slate-500">Đang tải hóa đơn...</p></Card> :
       items.length === 0 ? <EmptyState title="Chưa có hóa đơn" description="Tạo hóa đơn đầu tiên để bắt đầu quản lý tài chính." /> :
       <Card className="overflow-hidden p-0"><div className="overflow-x-auto"><table className="w-full min-w-[950px] text-left text-sm">
        <thead className="bg-slate-50 dark:bg-slate-900"><tr><th className="px-5 py-3">Hóa đơn</th><th className="px-5 py-3">Khách hàng</th><th className="px-5 py-3">Đến hạn</th><th className="px-5 py-3">Tổng</th><th className="px-5 py-3">Còn phải thu</th><th className="px-5 py-3">Trạng thái</th><th className="px-5 py-3">Xem</th></tr></thead>
        <tbody className="divide-y divide-slate-100 dark:divide-slate-800">{items.map(i => <tr key={i.id}>
          <td className="px-5 py-4"><p className="font-semibold">{i.invoiceNumber}</p><p className="text-xs text-slate-500">{i.issueDate}</p></td>
          <td className="px-5 py-4">{i.customerName}</td><td className="px-5 py-4">{i.dueDate}</td><td className="px-5 py-4 font-medium">{money(i.totalAmount)}</td><td className="px-5 py-4 font-medium">{money(i.remainingAmount)}</td>
          <td className="px-5 py-4"><Badge variant={variants[i.status]}>{labels[i.status]}</Badge></td>
          <td className="px-5 py-4"><Link to={'/finance/invoices/' + i.id}><Button variant="ghost"><Eye className="size-4" />Xem</Button></Link></td>
        </tr>)}</tbody>
       </table></div>
       {totalPages > 1 && <div className="flex items-center justify-between border-t border-slate-100 px-5 py-3 text-sm dark:border-slate-800"><span>Trang {page + 1} / {totalPages}</span><div className="flex gap-2"><Button variant="secondary" disabled={page === 0} onClick={() => setPage(v => v - 1)}>Trước</Button><Button variant="secondary" disabled={page >= totalPages - 1} onClick={() => setPage(v => v + 1)}>Sau</Button></div></div>}
       </Card>}
    </div>
  );
}
