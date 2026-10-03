import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { Building2, Plus, Search, UserRound } from 'lucide-react';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import { customersService } from './customers.service';
import type { Customer, CustomerType } from './customers.types';

const typeLabels: Record<CustomerType, string> = {
  COMPANY: 'Doanh nghiệp',
  INDIVIDUAL: 'Cá nhân',
};

export function CustomersPage() {
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [keyword, setKeyword] = useState('');
  const [active, setActive] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');
  const [form, setForm] = useState({
    code: '', name: '', type: 'COMPANY' as CustomerType, email: '', phone: '',
    taxCode: '', contactPerson: '', address: '',
  });

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const result = await customersService.list({
        page,
        size: 10,
        keyword,
        active: active === '' ? undefined : active === 'true',
      });
      setCustomers(result.content);
      setTotalPages(result.totalPages);
    } catch {
      setError('Không thể tải danh sách khách hàng.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { setPage(0); }, [keyword, active]);
  useEffect(() => { void load(); }, [keyword, active, page]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setSaving(true);
    setFormError('');
    try {
      await customersService.create(form);
      setShowForm(false);
      setForm({ code: '', name: '', type: 'COMPANY', email: '', phone: '', taxCode: '', contactPerson: '', address: '' });
      await load();
    } catch {
      setFormError('Không thể tạo khách hàng. Kiểm tra mã khách hàng hoặc mã số thuế.');
    } finally {
      setSaving(false);
    }
  };

  const remove = async (customer: Customer) => {
    if (!window.confirm(`Bạn có chắc muốn xóa khách hàng "${customer.name}"?`)) return;
    try {
      await customersService.remove(customer.id);
      await load();
    } catch {
      setError('Không thể xóa khách hàng.');
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Khách hàng</h1>
          <p className="mt-1 text-sm text-slate-500">Quản lý thông tin khách hàng và đối tác của doanh nghiệp.</p>
        </div>
        <Button onClick={() => setShowForm((value) => !value)}><Plus className="size-4" />{showForm ? 'Đóng' : 'Thêm khách hàng'}</Button>
      </div>

      {showForm && (
        <Card>
          <form onSubmit={submit} className="grid gap-4 md:grid-cols-2">
            <div><label className="text-sm font-medium">Mã khách hàng</label><input required value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} placeholder="CUS-001" className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Tên khách hàng</label><input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Loại</label><select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value as CustomerType })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"><option value="COMPANY">Doanh nghiệp</option><option value="INDIVIDUAL">Cá nhân</option></select></div>
            <div><label className="text-sm font-medium">Email</label><input type="email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Điện thoại</label><input value={form.phone} onChange={(e) => setForm({ ...form, phone: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Mã số thuế</label><input value={form.taxCode} onChange={(e) => setForm({ ...form, taxCode: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Người liên hệ</label><input value={form.contactPerson} onChange={(e) => setForm({ ...form, contactPerson: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Địa chỉ</label><input value={form.address} onChange={(e) => setForm({ ...form, address: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            {formError && <p className="md:col-span-2 text-sm text-red-600">{formError}</p>}
            <div className="md:col-span-2 flex justify-end"><Button type="submit" loading={saving}>Lưu khách hàng</Button></div>
          </form>
        </Card>
      )}

      <Card>
        <div className="grid gap-3 sm:grid-cols-[1fr_auto]">
          <div className="relative">
            <Search className="absolute left-3 top-2.5 size-4 text-slate-400" />
            <input aria-label="Tìm khách hàng" value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="Tìm mã, tên, email, điện thoại..." className="w-full rounded-xl border border-slate-200 py-2.5 pl-9 pr-3 text-sm dark:border-slate-700 dark:bg-slate-900" />
          </div>
          <select aria-label="Lọc trạng thái" value={active} onChange={(e) => setActive(e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Tất cả</option><option value="true">Đang hoạt động</option><option value="false">Ngừng hoạt động</option>
          </select>
        </div>
      </Card>

      {error ? <Card><p className="text-sm text-red-600">{error}</p></Card> :
       loading ? <Card><p className="text-sm text-slate-500">Đang tải khách hàng...</p></Card> :
       customers.length === 0 ? <EmptyState title="Chưa có khách hàng" description="Thêm khách hàng đầu tiên để bắt đầu quản lý." /> :
       <Card className="overflow-hidden p-0">
         <div className="overflow-x-auto">
           <table className="w-full min-w-[900px] text-left text-sm">
             <thead className="bg-slate-50 dark:bg-slate-900"><tr><th className="px-5 py-3">Khách hàng</th><th className="px-5 py-3">Loại</th><th className="px-5 py-3">Liên hệ</th><th className="px-5 py-3">Mã số thuế</th><th className="px-5 py-3">Trạng thái</th><th className="px-5 py-3">Thao tác</th></tr></thead>
             <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
               {customers.map((customer) => <tr key={customer.id} className="hover:bg-slate-50 dark:hover:bg-slate-900/50">
                 <td className="px-5 py-4"><div className="flex items-center gap-3">{customer.type === 'COMPANY' ? <Building2 className="size-4 text-blue-600" /> : <UserRound className="size-4 text-blue-600" />}<div><p className="font-semibold">{customer.name}</p><p className="text-xs text-slate-500">{customer.code}</p></div></div></td>
                 <td className="px-5 py-4">{typeLabels[customer.type]}</td>
                 <td className="px-5 py-4"><p>{customer.phone || '—'}</p><p className="text-xs text-slate-500">{customer.email || '—'}</p></td>
                 <td className="px-5 py-4">{customer.taxCode || '—'}</td>
                 <td className="px-5 py-4"><Badge variant={customer.active ? 'success' : 'neutral'}>{customer.active ? 'Hoạt động' : 'Ngừng hoạt động'}</Badge></td>
                 <td className="px-5 py-4"><Button variant="ghost" onClick={() => void remove(customer)}>Xóa</Button></td>
               </tr>)}
             </tbody>
           </table>
         </div>
         {totalPages > 1 && <div className="flex items-center justify-between border-t border-slate-100 px-5 py-3 text-sm dark:border-slate-800"><span>Trang {page + 1} / {totalPages}</span><div className="flex gap-2"><Button variant="secondary" disabled={page === 0} onClick={() => setPage((v) => v - 1)}>Trước</Button><Button variant="secondary" disabled={page >= totalPages - 1} onClick={() => setPage((v) => v + 1)}>Sau</Button></div></div>}
       </Card>}
    </div>
  );
}
