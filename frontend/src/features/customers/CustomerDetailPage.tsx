import { useEffect, useState } from 'react';
import { ArrowLeft, Building2, UserRound } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import { Button } from '../../components/ui/Button';
import { customersService } from './customers.service';
import type { Customer } from './customers.types';

export function CustomerDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [customer, setCustomer] = useState<Customer | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!id) return;
    setLoading(true);
    customersService.get(id)
      .then(setCustomer)
      .catch(() => setError('Không thể tải thông tin khách hàng.'))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) return <Card><p className="text-sm text-slate-500">Đang tải khách hàng...</p></Card>;
  if (error || !customer) return <EmptyState title="Không tìm thấy khách hàng" description={error || 'Khách hàng không tồn tại.'} />;

  return (
    <div className="space-y-6">
      <Link to="/customers" className="inline-flex items-center gap-2 text-sm font-medium text-slate-500 hover:text-blue-600">
        <ArrowLeft className="size-4" /> Quay lại khách hàng
      </Link>

      <Card>
        <div className="flex flex-col justify-between gap-5 md:flex-row md:items-start">
          <div className="flex gap-4">
            <div className="rounded-2xl bg-blue-50 p-4 text-blue-600 dark:bg-blue-950/40 dark:text-blue-300">
              {customer.type === 'COMPANY' ? <Building2 className="size-7" /> : <UserRound className="size-7" />}
            </div>
            <div>
              <div className="flex flex-wrap items-center gap-2">
                <span className="text-xs font-semibold text-slate-500">{customer.code}</span>
                <Badge variant={customer.active ? 'success' : 'neutral'}>{customer.active ? 'Hoạt động' : 'Ngừng hoạt động'}</Badge>
              </div>
              <h1 className="mt-2 text-2xl font-bold">{customer.name}</h1>
              <p className="mt-1 text-sm text-slate-500">{customer.type === 'COMPANY' ? 'Doanh nghiệp' : 'Cá nhân'}</p>
            </div>
          </div>
          <Button variant="secondary" disabled>Chỉnh sửa</Button>
        </div>
      </Card>

      <div className="grid gap-6 lg:grid-cols-2">
        <Card>
          <h2 className="font-semibold">Thông tin liên hệ</h2>
          <dl className="mt-4 space-y-4 text-sm">
            <div><dt className="text-slate-500">Email</dt><dd className="mt-1 font-medium">{customer.email || '—'}</dd></div>
            <div><dt className="text-slate-500">Điện thoại</dt><dd className="mt-1 font-medium">{customer.phone || '—'}</dd></div>
            <div><dt className="text-slate-500">Người liên hệ</dt><dd className="mt-1 font-medium">{customer.contactPerson || '—'}</dd></div>
            <div><dt className="text-slate-500">Địa chỉ</dt><dd className="mt-1 font-medium">{customer.address || '—'}</dd></div>
          </dl>
        </Card>

        <Card>
          <h2 className="font-semibold">Thông tin doanh nghiệp</h2>
          <dl className="mt-4 space-y-4 text-sm">
            <div><dt className="text-slate-500">Mã số thuế</dt><dd className="mt-1 font-medium">{customer.taxCode || '—'}</dd></div>
            <div><dt className="text-slate-500">Ngày tạo</dt><dd className="mt-1 font-medium">{new Date(customer.createdAt).toLocaleString('vi-VN')}</dd></div>
            <div><dt className="text-slate-500">Cập nhật lần cuối</dt><dd className="mt-1 font-medium">{new Date(customer.updatedAt).toLocaleString('vi-VN')}</dd></div>
          </dl>
        </Card>
      </div>

      <Card>
        <h2 className="font-semibold">Dự án của khách hàng</h2>
        <div className="mt-4 rounded-xl border border-dashed border-slate-200 p-6 text-sm text-slate-500 dark:border-slate-700">
          Chưa liên kết dự án với khách hàng. Quan hệ Customer → Project sẽ được triển khai ở bước nghiệp vụ dự án tiếp theo.
        </div>
      </Card>
    </div>
  );
}
