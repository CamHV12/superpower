import { useEffect, useMemo, useState } from 'react';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Badge } from '../components/ui/Badge';
import { Card } from '../components/ui/Card';
import { financeService } from '../finance/finance.service';

const activities = [
  ['Nguyễn Minh Anh', 'Tạo dự án Website E-commerce', '10 phút trước', 'info'],
  ['Trần Quốc Bảo', 'Hoàn thành task Thiết kế database', '32 phút trước', 'success'],
  ['Lê Thu Hà', 'Tạo hóa đơn INV-2026-018', '1 giờ trước', 'warning'],
  ['Phạm Đức Long', 'Cập nhật tiến độ Mobile App lên 80%', '2 giờ trước', 'info'],
] as const;

const formatMoney = (value: number) => new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND',
  maximumFractionDigits: 0,
}).format(value);

export function DashboardPage() {
  const [period, setPeriod] = useState('6 tháng gần nhất');
  const [summary, setSummary] = useState<Awaited<ReturnType<typeof financeService.summary>> | null>(null);
  const [monthly, setMonthly] = useState<Awaited<ReturnType<typeof financeService.monthly>>>([]);
  const [financeError, setFinanceError] = useState('');

  const periodLabel = useMemo(() => period, [period]);

  useEffect(() => {
    const months = period.startsWith('12') ? 12 : 6;
    Promise.all([financeService.summary(), financeService.monthly(months)])
      .then(([summaryData, monthlyData]) => {
        setSummary(summaryData);
        setMonthly(monthlyData);
      })
      .catch(() => setFinanceError('Chưa thể tải dữ liệu tài chính.'));
  }, [period]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Tổng quan doanh nghiệp</h1>
          <p className="mt-1 text-sm text-slate-500">Theo dõi tình hình vận hành và tài chính của doanh nghiệp.</p>
        </div>
        <select value={period} onChange={(event) => setPeriod(event.target.value)} className="rounded-xl border border-slate-200 bg-white px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-950">
          <option>6 tháng gần nhất</option>
          <option>12 tháng gần nhất</option>
        </select>
      </div>

      <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
        {[
          ['Tổng giá trị hóa đơn', summary ? formatMoney(summary.totalInvoiced) : '—'],
          ['Đã thanh toán', summary ? formatMoney(summary.totalPaid) : '—'],
          ['Còn phải thu', summary ? formatMoney(summary.totalReceivable) : '—'],
          ['Hóa đơn quá hạn', summary ? `${summary.overdueInvoices} · ${formatMoney(summary.overdueAmount)}` : '—'],
        ].map(([label, value]) => (
          <Card key={label}>
            <p className="text-sm text-slate-500">{label}</p>
            <p className="mt-2 text-xl font-bold">{value}</p>
          </Card>
        ))}
      </section>

      {financeError && (
        <Card>
          <p className="text-sm text-red-600">{financeError}</p>
        </Card>
      )}

      <section className="grid gap-6 xl:grid-cols-[2fr_1fr]">
        <Card>
          <div className="mb-5 flex items-center justify-between">
            <div>
              <h2 className="font-semibold">Tiền đã thu</h2>
              <p className="text-xs text-slate-500">{periodLabel}</p>
            </div>
            <Badge variant="success">Dữ liệu thật</Badge>
          </div>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={monthly}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => [formatMoney(Number(value)), 'Đã thu']} />
                <Area type="monotone" dataKey="paidAmount" stroke="#2563eb" fill="#2563eb" fillOpacity={0.12} name="Đã thu" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </Card>

        <Card>
          <h2 className="font-semibold">Hoạt động gần đây</h2>
          <div className="mt-4 space-y-4">
            {activities.map(([user, action, time, variant]) => (
              <div key={user + action} className="flex gap-3">
                <div className="mt-1 size-2 shrink-0 rounded-full bg-blue-500" />
                <div className="min-w-0">
                  <p className="text-sm font-medium">{user}</p>
                  <p className="text-sm text-slate-500">{action}</p>
                  <p className="mt-1 text-xs text-slate-400">{time}</p>
                </div>
                <Badge variant={variant}>{variant === 'success' ? 'Xong' : variant === 'warning' ? 'Tài chính' : 'Cập nhật'}</Badge>
              </div>
            ))}
          </div>
        </Card>
      </section>
    </div>
  );
}
