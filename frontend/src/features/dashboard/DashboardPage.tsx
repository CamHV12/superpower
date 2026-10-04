import { useEffect, useState } from 'react';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { financeService } from '../finance/finance.service';
import { dashboardService, type DashboardOverview } from './dashboard.service';

const formatMoney = (value: number) => new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND',
  maximumFractionDigits: 0,
}).format(value);

export function DashboardPage() {
  const [period, setPeriod] = useState('6 tháng gần nhất');
  const [summary, setSummary] = useState<Awaited<ReturnType<typeof financeService.summary>> | null>(null);
  const [monthly, setMonthly] = useState<Awaited<ReturnType<typeof financeService.monthly>>>([]);
  const [overview, setOverview] = useState<DashboardOverview | null>(null);
  const [operational, setOperational] = useState<Awaited<ReturnType<typeof dashboardService.operational>> | null>(null);
  const [dashboardError, setDashboardError] = useState('');

  useEffect(() => {
    const months = period.startsWith('12') ? 12 : 6;
    setDashboardError('');

    Promise.all([
      financeService.summary(),
      financeService.monthly(months),
      dashboardService.overview(),
      dashboardService.operational(),
    ])
      .then(([summaryData, monthlyData, overviewData, operationalData]) => {
        setSummary(summaryData);
        setMonthly(monthlyData);
        setOverview(overviewData);
        setOperational(operationalData);
      })
      .catch(() => setDashboardError('Chưa thể tải đầy đủ dữ liệu dashboard.'));
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

      {dashboardError && (
        <Card>
          <p className="text-sm text-red-600">{dashboardError}</p>
        </Card>
      )}

      <section>
        <div className="mb-3 flex items-center justify-between">
          <h2 className="font-semibold">Vận hành</h2>
          <Badge variant="success">Dữ liệu thật</Badge>
        </div>
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
          {[
            ['Nhân sự đang hoạt động', overview ? `${overview.activeEmployees} / ${overview.totalEmployees}` : '—'],
            ['Dự án đang hoạt động', overview ? `${overview.activeProjects} / ${overview.totalProjects}` : '—'],
            ['Khách hàng đang hoạt động', overview ? `${overview.activeCustomers} / ${overview.totalCustomers}` : '—'],
            ['Task quá hạn', overview ? `${overview.overdueTasks} / ${overview.totalTasks}` : '—'],
          ].map(([label, value]) => (
            <Card key={label}>
              <p className="text-sm text-slate-500">{label}</p>
              <p className="mt-2 text-2xl font-bold">{value}</p>
            </Card>
          ))}
        </div>
      </section>

      <section className="grid gap-4 lg:grid-cols-2">
        <Card>
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h2 className="font-semibold">Trạng thái dự án</h2>
              <p className="text-xs text-slate-500">Phân bổ dự án hiện tại</p>
            </div>
            <Badge variant="success">Dữ liệu thật</Badge>
          </div>
          <div className="space-y-3">
            {(operational?.projectStatuses ?? []).map(item => {
              const total = operational?.projectStatuses.reduce((sum, current) => sum + current.count, 0) || 1;
              const percent = Math.round(item.count * 100 / total);
              return (
                <div key={item.status}>
                  <div className="mb-1 flex justify-between text-sm">
                    <span>{item.status}</span><span className="font-medium">{item.count}</span>
                  </div>
                  <div className="h-2 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
                    <div className="h-full rounded-full bg-slate-500" style={{ width: `${percent}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </Card>

        <Card>
          <div className="mb-4 flex items-center justify-between">
            <div>
              <h2 className="font-semibold">Trạng thái task</h2>
              <p className="text-xs text-slate-500">Phân bổ công việc hiện tại</p>
            </div>
            <Badge variant="success">Dữ liệu thật</Badge>
          </div>
          <div className="space-y-3">
            {(operational?.taskStatuses ?? []).map(item => {
              const total = operational?.taskStatuses.reduce((sum, current) => sum + current.count, 0) || 1;
              const percent = Math.round(item.count * 100 / total);
              return (
                <div key={item.status}>
                  <div className="mb-1 flex justify-between text-sm">
                    <span>{item.status}</span><span className="font-medium">{item.count}</span>
                  </div>
                  <div className="h-2 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800">
                    <div className="h-full rounded-full bg-slate-500" style={{ width: `${percent}%` }} />
                  </div>
                </div>
              );
            })}
          </div>
        </Card>
      </section>

      <section>
        <div className="mb-3 flex items-center justify-between">
          <h2 className="font-semibold">Tài chính</h2>
          <Badge variant="success">Dữ liệu thật</Badge>
        </div>
        <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-6">
          {[
            ['Tổng giá trị hóa đơn', summary ? formatMoney(summary.totalInvoiced) : '—'],
            ['Đã thanh toán', summary ? formatMoney(summary.totalPaid) : '—'],
            ['Còn phải thu', summary ? formatMoney(summary.totalReceivable) : '—'],
            ['Tổng chi phí', summary ? formatMoney(summary.totalExpense) : '—'],
            ['Dòng tiền ròng', summary ? formatMoney(summary.netCashFlow) : '—'],
            ['Hóa đơn quá hạn', summary ? `${summary.overdueInvoices} · ${formatMoney(summary.overdueAmount)}` : '—'],
          ].map(([label, value]) => (
            <Card key={label}>
              <p className="text-sm text-slate-500">{label}</p>
              <p className="mt-2 text-xl font-bold">{value}</p>
            </Card>
          ))}
        </div>
      </section>

      <Card>
        <div className="mb-5 flex items-center justify-between">
          <div>
            <h2 className="font-semibold">Dòng tiền theo tháng</h2>
            <p className="text-xs text-slate-500">{period}</p>
          </div>
          <Badge variant="success">Dữ liệu thật</Badge>
        </div>
        <div className="h-72">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={monthly}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="month" />
              <YAxis />
              <Tooltip formatter={(value, name) => [formatMoney(Number(value)), name === 'paidAmount' ? 'Đã thu' : 'Chi phí']} />
              <Area type="monotone" dataKey="paidAmount" name="Đã thu" />
              <Area type="monotone" dataKey="expenseAmount" name="Chi phí" />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </Card>
    </div>
  );
}
