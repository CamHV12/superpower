import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { Ban, Plus, RotateCcw, Search } from 'lucide-react';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import { financeService } from './finance.service';
import type { Expense, ExpenseStatus, PaymentMethod } from './finance.types';

const methods: Record<PaymentMethod, string> = {
  CASH: 'Tiền mặt',
  BANK_TRANSFER: 'Chuyển khoản',
  CREDIT_CARD: 'Thẻ',
  OTHER: 'Khác',
};

const money = (n: number) => new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND',
  maximumFractionDigits: 0,
}).format(n);

const today = () => new Date().toISOString().slice(0, 10);

type ExpenseFilters = {
  keyword: string;
  category: string;
  status: ExpenseStatus | '';
  paymentMethod: PaymentMethod | '';
  fromDate: string;
  toDate: string;
};

const emptyFilters: ExpenseFilters = {
  keyword: '',
  category: '',
  status: '',
  paymentMethod: '',
  fromDate: '',
  toDate: '',
};

export function ExpensesPage() {
  const [items, setItems] = useState<Expense[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [filters, setFilters] = useState<ExpenseFilters>(emptyFilters);
  const [appliedFilters, setAppliedFilters] = useState<ExpenseFilters>(emptyFilters);
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');
  const [form, setForm] = useState({
    category: '', amount: 0, expenseDate: today(), vendor: '',
    paymentMethod: 'BANK_TRANSFER' as PaymentMethod, notes: '',
  });

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await financeService.listExpenses({
        page,
        size: 10,
        keyword: appliedFilters.keyword || undefined,
        category: appliedFilters.category || undefined,
        status: appliedFilters.status || undefined,
        paymentMethod: appliedFilters.paymentMethod || undefined,
        fromDate: appliedFilters.fromDate || undefined,
        toDate: appliedFilters.toDate || undefined,
      });
      setItems(result.content);
      setTotalPages(result.totalPages);
    } catch {
      setError('Không thể tải danh sách khoản chi.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { void load(); }, [page, appliedFilters]);

  const applyFilters = (event: FormEvent) => {
    event.preventDefault();
    setPage(0);
    setAppliedFilters({ ...filters });
  };

  const resetFilters = () => {
    setPage(0);
    setFilters(emptyFilters);
    setAppliedFilters(emptyFilters);
  };

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setFormError('');
    try {
      await financeService.createExpense({
        category: form.category,
        amount: form.amount,
        expenseDate: form.expenseDate,
        vendor: form.vendor || undefined,
        paymentMethod: form.paymentMethod,
        notes: form.notes || undefined,
      });
      setShowForm(false);
      setForm({ category: '', amount: 0, expenseDate: today(), vendor: '', paymentMethod: 'BANK_TRANSFER', notes: '' });
      await load();
    } catch {
      setFormError('Không thể ghi nhận khoản chi. Kiểm tra lại dữ liệu.');
    } finally {
      setSaving(false);
    }
  };

  const cancel = async (expense: Expense) => {
    if (expense.status === 'CANCELLED') return;
    if (!window.confirm('Bạn có chắc muốn hủy khoản chi này? Dữ liệu sẽ được giữ lại để đảm bảo lịch sử tài chính.')) return;
    try {
      await financeService.cancelExpense(expense.id);
      await load();
    } catch {
      setError('Không thể hủy khoản chi.');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Tài chính · Chi phí</h1>
          <p className="mt-1 text-sm text-slate-500">Ghi nhận và theo dõi các khoản chi thực tế của doanh nghiệp.</p>
        </div>
        <Button onClick={() => setShowForm(value => !value)}>
          <Plus className="size-4" />{showForm ? 'Đóng' : 'Ghi nhận khoản chi'}
        </Button>
      </div>

      <Card>
        <form onSubmit={applyFilters} className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          <div className="xl:col-span-2">
            <label className="text-sm font-medium">Tìm kiếm</label>
            <div className="relative mt-1">
              <Search className="pointer-events-none absolute left-3 top-2.5 size-4 text-slate-400" />
              <input
                value={filters.keyword}
                onChange={e => setFilters({ ...filters, keyword: e.target.value })}
                placeholder="Danh mục, nhà cung cấp, ghi chú..."
                className="w-full rounded-xl border border-slate-200 py-2 pl-9 pr-3 text-sm dark:border-slate-700 dark:bg-slate-900"
              />
            </div>
          </div>
          <div>
            <label className="text-sm font-medium">Danh mục</label>
            <input
              value={filters.category}
              onChange={e => setFilters({ ...filters, category: e.target.value })}
              placeholder="Văn phòng, Marketing..."
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"
            />
          </div>
          <div>
            <label className="text-sm font-medium">Trạng thái</label>
            <select
              value={filters.status}
              onChange={e => setFilters({ ...filters, status: e.target.value as ExpenseStatus | '' })}
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"
            >
              <option value="">Tất cả</option>
              <option value="RECORDED">Đã ghi nhận</option>
              <option value="CANCELLED">Đã hủy</option>
            </select>
          </div>
          <div>
            <label className="text-sm font-medium">Phương thức</label>
            <select
              value={filters.paymentMethod}
              onChange={e => setFilters({ ...filters, paymentMethod: e.target.value as PaymentMethod | '' })}
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"
            >
              <option value="">Tất cả</option>
              {Object.entries(methods).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
            </select>
          </div>
          <div>
            <label className="text-sm font-medium">Từ ngày</label>
            <input
              type="date"
              value={filters.fromDate}
              onChange={e => setFilters({ ...filters, fromDate: e.target.value })}
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"
            />
          </div>
          <div>
            <label className="text-sm font-medium">Đến ngày</label>
            <input
              type="date"
              value={filters.toDate}
              onChange={e => setFilters({ ...filters, toDate: e.target.value })}
              className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"
            />
          </div>
          <div className="flex items-end gap-2">
            <Button type="submit"><Search className="size-4" />Lọc</Button>
            <Button type="button" variant="secondary" onClick={resetFilters}>
              <RotateCcw className="size-4" />Đặt lại
            </Button>
          </div>
        </form>
      </Card>

      {showForm && (
        <Card>
          <form onSubmit={submit} className="grid gap-4 md:grid-cols-2">
            <div>
              <label className="text-sm font-medium">Danh mục</label>
              <input required maxLength={100} value={form.category} onChange={e => setForm({ ...form, category: e.target.value })} placeholder="Văn phòng, Nhân sự, Marketing..." className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            <div>
              <label className="text-sm font-medium">Số tiền</label>
              <input required min="0.01" step="1000" type="number" value={form.amount} onChange={e => setForm({ ...form, amount: Number(e.target.value) })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            <div>
              <label className="text-sm font-medium">Ngày chi</label>
              <input required type="date" value={form.expenseDate} onChange={e => setForm({ ...form, expenseDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            <div>
              <label className="text-sm font-medium">Nhà cung cấp</label>
              <input value={form.vendor} onChange={e => setForm({ ...form, vendor: e.target.value })} placeholder="Tên nhà cung cấp" className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            <div>
              <label className="text-sm font-medium">Phương thức thanh toán</label>
              <select value={form.paymentMethod} onChange={e => setForm({ ...form, paymentMethod: e.target.value as PaymentMethod })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
                {Object.entries(methods).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
              </select>
            </div>
            <textarea value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })} placeholder="Ghi chú" rows={2} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            {formError && <p className="md:col-span-2 text-sm text-red-600">{formError}</p>}
            <div className="md:col-span-2 flex justify-end"><Button type="submit" loading={saving}>Lưu khoản chi</Button></div>
          </form>
        </Card>
      )}

      {error ? <Card><p className="text-sm text-red-600">{error}</p></Card> :
       loading ? <Card><p className="text-sm text-slate-500">Đang tải khoản chi...</p></Card> :
       items.length === 0 ? <EmptyState title="Không tìm thấy khoản chi" description="Thử thay đổi bộ lọc hoặc ghi nhận một khoản chi mới." /> :
       <Card className="overflow-hidden p-0">
         <div className="overflow-x-auto">
           <table className="w-full min-w-[850px] text-left text-sm">
             <thead className="bg-slate-50 dark:bg-slate-900">
               <tr><th className="px-5 py-3">Ngày</th><th className="px-5 py-3">Danh mục</th><th className="px-5 py-3">Nhà cung cấp</th><th className="px-5 py-3">Phương thức</th><th className="px-5 py-3">Số tiền</th><th className="px-5 py-3">Trạng thái</th><th className="px-5 py-3">Thao tác</th></tr>
             </thead>
             <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
               {items.map(item => <tr key={item.id}>
                 <td className="px-5 py-4">{item.expenseDate}</td>
                 <td className="px-5 py-4 font-medium">{item.category}</td>
                 <td className="px-5 py-4">{item.vendor || '—'}</td>
                 <td className="px-5 py-4">{methods[item.paymentMethod]}</td>
                 <td className="px-5 py-4 font-semibold">{money(item.amount)}</td>
                 <td className="px-5 py-4"><Badge variant={item.status === 'RECORDED' ? 'success' : 'danger'}>{item.status === 'RECORDED' ? 'Đã ghi nhận' : 'Đã hủy'}</Badge></td>
                 <td className="px-5 py-4"><Button variant="ghost" disabled={item.status === 'CANCELLED'} onClick={() => void cancel(item)}><Ban className="size-4" />Hủy</Button></td>
               </tr>)}
             </tbody>
           </table>
         </div>
         {totalPages > 1 && <div className="flex items-center justify-between border-t border-slate-100 px-5 py-3 text-sm dark:border-slate-800"><span>Trang {page + 1} / {totalPages}</span><div className="flex gap-2"><Button variant="secondary" disabled={page === 0} onClick={() => setPage(value => value - 1)}>Trước</Button><Button variant="secondary" disabled={page >= totalPages - 1} onClick={() => setPage(value => value + 1)}>Sau</Button></div></div>}
       </Card>}
    </div>
  );
}
