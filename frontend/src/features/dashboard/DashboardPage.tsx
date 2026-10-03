import { useMemo, useState } from 'react';
import { ArrowDownRight, ArrowUpRight, BriefcaseBusiness, CircleDollarSign, Users, WalletCards } from 'lucide-react';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Badge } from '../components/ui/Badge';
import { Card } from '../components/ui/Card';

const revenueData = [
  { month: 'T1', revenue: 420, expense: 280 },
  { month: 'T2', revenue: 510, expense: 310 },
  { month: 'T3', revenue: 470, expense: 295 },
  { month: 'T4', revenue: 620, expense: 340 },
  { month: 'T5', revenue: 710, expense: 390 },
  { month: 'T6', revenue: 780, expense: 430 },
];

const activities = [
  ['Nguyễn Minh Anh', 'Tạo dự án Website E-commerce', '10 phút trước', 'info'],
  ['Trần Quốc Bảo', 'Hoàn thành task Thiết kế database', '32 phút trước', 'success'],
  ['Lê Thu Hà', 'Tạo hóa đơn INV-2026-018', '1 giờ trước', 'warning'],
  ['Phạm Đức Long', 'Cập nhật tiến độ Mobile App lên 80%', '2 giờ trước', 'info'],
] as const;

const kpis = [
  { label: 'Doanh thu tháng', value: '780 triệu', change: '+12,5%', icon: CircleDollarSign, positive: true },
  { label: 'Chi phí tháng', value: '430 triệu', change: '+4,8%', icon: WalletCards, positive: false },
  { label: 'Nhân sự', value: '128', change: '+6 người', icon: Users, positive: true },
  { label: 'Dự án đang chạy', value: '24', change: '+3 dự án', icon: BriefcaseBusiness, positive: true },
];

export function DashboardPage() {
  const [period, setPeriod] = useState('6 tháng gần nhất');
  const periodLabel = useMemo(() => period, [period]);

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
        {kpis.map(({ label, value, change, icon: Icon, positive }) => (
          <Card key={label}>
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-slate-500">{label}</p>
                <p className="mt-2 text-2xl font-bold">{value}</p>
                <span className={`mt-2 inline-flex items-center gap-1 text-xs font-semibold ${positive ? 'text-emerald-600' : 'text-amber-600'}`}>
                  {positive ? <ArrowUpRight className="size-3.5" /> : <ArrowDownRight className="size-3.5" />}{change}
                </span>
              </div>
              <div className="rounded-xl bg-blue-50 p-3 text-blue-600 dark:bg-blue-950/40 dark:text-blue-300"><Icon className="size-5" /></div>
            </div>
          </Card>
        ))}
      </section>

      <section className="grid gap-6 xl:grid-cols-[2fr_1fr]">
        <Card>
          <div className="mb-5 flex items-center justify-between">
            <div><h2 className="font-semibold">Doanh thu & chi phí</h2><p className="text-xs text-slate-500">{periodLabel}</p></div>
            <Badge variant="success">Lợi nhuận dương</Badge>
          </div>
          <div className="h-72">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={revenueData}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="month" />
                <YAxis />
                <Tooltip formatter={(value) => [`${value} triệu`, '']} />
                <Area type="monotone" dataKey="revenue" stroke="#2563eb" fill="#2563eb" fillOpacity={0.12} name="Doanh thu" />
                <Area type="monotone" dataKey="expense" stroke="#f59e0b" fill="#f59e0b" fillOpacity={0.10} name="Chi phí" />
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
