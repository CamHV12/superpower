import { useEffect, useState } from 'react';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { getOperationalReport, getPerformanceReport, reportsService, type ReportResponse, type OperationalReport, type PerformanceReport } from './reports.service';

const formatMoney = (value: number) => new Intl.NumberFormat('vi-VN', {
  style: 'currency',
  currency: 'VND',
  maximumFractionDigits: 0,
}).format(value);

const toDateInput = (date: Date) => date.toISOString().slice(0, 10);

export function ReportsPage() {
  const now = new Date();
  const [from, setFrom] = useState(toDateInput(new Date(now.getFullYear(), now.getMonth() - 5, 1)));
  const [to, setTo] = useState(toDateInput(now));
  const [report, setReport] = useState<ReportResponse | null>(null);
  const [operational, setOperational] = useState<OperationalReport | null>(null);
  const [performance, setPerformance] = useState<PerformanceReport | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    setError('');
    Promise.all([reportsService.summary(from, to), getOperationalReport(), getPerformanceReport()])
      .then(([summary, operations, performanceReport]) => { setReport(summary); setOperational(operations); setPerformance(performanceReport); })
      .catch(() => setError('Không thể tải báo cáo.'))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 lg:flex-row lg:items-end">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Báo cáo</h1>
          <p className="mt-1 text-sm text-slate-500">Phân tích tổng hợp tài chính và vận hành theo khoảng thời gian.</p>
        </div>
        <div className="flex flex-wrap items-end gap-2">
          <label className="text-sm">
            <span className="mb-1 block text-slate-500">Từ ngày</span>
            <input type="date" value={from} onChange={e => setFrom(e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 dark:border-slate-700 dark:bg-slate-950" />
          </label>
          <label className="text-sm">
            <span className="mb-1 block text-slate-500">Đến ngày</span>
            <input type="date" value={to} onChange={e => setTo(e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 dark:border-slate-700 dark:bg-slate-950" />
          </label>
          <button onClick={load} className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white">Xem báo cáo</button>
        </div>
      </div>

      {loading && <Card><p className="text-sm text-slate-500">Đang tải báo cáo...</p></Card>}
      {error && <Card><p className="text-sm text-red-600">{error}</p></Card>}

      {report && !loading && (
        <>
          <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-5">
            {[
              ['Doanh thu hóa đơn', formatMoney(report.summary.invoicedAmount)],
              ['Đã thu', formatMoney(report.summary.paidAmount)],
              ['Chi phí', formatMoney(report.summary.expenseAmount)],
              ['Còn phải thu', formatMoney(report.summary.receivableAmount)],
              ['Dòng tiền ròng', formatMoney(report.summary.netCashFlow)],
            ].map(([label, value]) => (
              <Card key={label}>
                <p className="text-sm text-slate-500">{label}</p>
                <p className="mt-2 text-xl font-bold">{value}</p>
              </Card>
            ))}
          </div>

          <section className="grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
            {[
              ['Hóa đơn', report.summary.invoiceCount],
              ['Hóa đơn đã thanh toán', report.summary.paidInvoiceCount],
              ['Hóa đơn quá hạn', report.summary.overdueInvoiceCount],
              ['Dự án đang hoạt động', report.summary.activeProjectCount],
              ['Task hoàn thành', report.summary.completedTaskCount],
              ['Tổng task', report.summary.taskCount],
              ['Khách hàng hoạt động', report.summary.activeCustomerCount],
              ['Tổng khách hàng', report.summary.customerCount],
            ].map(([label, value]) => (
              <Card key={label}>
                <p className="text-sm text-slate-500">{label}</p>
                <p className="mt-2 text-2xl font-bold">{value}</p>
              </Card>
            ))}
          </section>

          {operational && (
            <div className="grid gap-4 xl:grid-cols-2">
              <Card>
                <h2 className="mb-4 font-semibold">Phân bổ trạng thái Project</h2>
                <div className="space-y-3">
                  {operational.projectStatuses.map(item => (
                    <div key={item.status} className="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900">
                      <span>{item.status}</span><strong>{item.count}</strong>
                    </div>
                  ))}
                </div>
              </Card>
              <Card>
                <h2 className="mb-4 font-semibold">Phân bổ trạng thái Task</h2>
                <div className="space-y-3">
                  {operational.taskStatuses.map(item => (
                    <div key={item.status} className="flex items-center justify-between rounded-lg bg-slate-50 px-3 py-2 dark:bg-slate-900">
                      <span>{item.status}</span><strong>{item.count}</strong>
                    </div>
                  ))}
                </div>
              </Card>
            </div>
          )}

          {operational && (
            <div className="grid gap-4 xl:grid-cols-2">
              <Card>
                <h2 className="mb-4 font-semibold">Hiệu suất nhân viên</h2>
                <div className="space-y-3">
                  {operational.employeePerformance.slice(0, 10).map(item => (
                    <div key={item.employeeId} className="rounded-xl border border-slate-100 p-3 dark:border-slate-800">
                      <div className="flex justify-between"><span className="font-medium">{item.employeeName}</span><span>{item.completedTasks}/{item.totalTasks} task</span></div>
                      <div className="mt-1 text-xs text-slate-500">Quá hạn: {item.overdueTasks} · Ước tính: {item.estimatedHours}h · Thực tế: {item.actualHours}h</div>
                    </div>
                  ))}
                </div>
              </Card>
              <Card>
                <h2 className="mb-4 font-semibold">Hiệu suất khách hàng</h2>
                <div className="space-y-3">
                  {operational.customerPerformance.slice(0, 10).map(item => (
                    <div key={item.customerId} className="flex items-center justify-between rounded-xl border border-slate-100 p-3 dark:border-slate-800">
                      <div><div className="font-medium">{item.customerName}</div><div className="text-xs text-slate-500">{item.activeProjects}/{item.projects} project đang hoạt động</div></div>
                      <strong>{formatMoney(item.budget)}</strong>
                    </div>
                  ))}
                </div>
              </Card>
            </div>
          )}

          {performance && (
            <div className="grid gap-4 xl:grid-cols-2">
              <Card>
                <div className="mb-4 flex items-center justify-between"><div><h2 className="font-semibold">Hiệu suất dự án</h2><p className="text-xs text-slate-500">Tiến độ, task và giờ thực tế theo từng dự án.</p></div><Badge variant="success">Dữ liệu thật</Badge></div>
                <div className="space-y-3">
                  {performance.projects.slice(0, 10).map(item => (
                    <div key={item.projectId} className="rounded-xl border border-slate-100 p-3 dark:border-slate-800">
                      <div className="flex items-center justify-between gap-3"><div><div className="font-medium">{item.code} · {item.name}</div><div className="text-xs text-slate-500">{item.customerName} · {item.status}</div></div><strong>{item.progress}%</strong></div>
                      <div className="mt-2 h-2 overflow-hidden rounded-full bg-slate-100 dark:bg-slate-800"><div className="h-full rounded-full bg-slate-700" style={{ width: item.progress + '%' }} /></div>
                      <div className="mt-2 text-xs text-slate-500">Task: {item.completedTasks}/{item.taskCount} · Quá hạn: {item.overdueTasks} · Giờ: {item.actualHours}/{item.estimatedHours}h · Ngân sách: {formatMoney(item.budget)}</div>
                    </div>
                  ))}
                </div>
              </Card>
              <Card>
                <div className="mb-4 flex items-center justify-between"><div><h2 className="font-semibold">Báo cáo khách hàng</h2><p className="text-xs text-slate-500">Dự án, doanh thu hóa đơn, đã thu và công nợ.</p></div><Badge variant="success">Dữ liệu thật</Badge></div>
                <div className="space-y-3">
                  {performance.customers.slice(0, 10).map(item => (
                    <div key={item.customerId} className="rounded-xl border border-slate-100 p-3 dark:border-slate-800">
                      <div className="flex items-center justify-between gap-3"><div><div className="font-medium">{item.code} · {item.name}</div><div className="text-xs text-slate-500">{item.activeProjects}/{item.projectCount} project đang hoạt động</div></div><strong>{formatMoney(item.receivableAmount)}</strong></div>
                      <div className="mt-2 grid grid-cols-3 gap-2 text-xs text-slate-500"><span>Hóa đơn<br/><b className="text-slate-700 dark:text-slate-200">{formatMoney(item.invoicedAmount)}</b></span><span>Đã thu<br/><b className="text-slate-700 dark:text-slate-200">{formatMoney(item.paidAmount)}</b></span><span>Ngân sách<br/><b className="text-slate-700 dark:text-slate-200">{formatMoney(item.projectBudget)}</b></span></div>
                    </div>
                  ))}
                </div>
              </Card>
            </div>
          )}
          <Card>
            <div className="mb-5 flex items-center justify-between">
              <div>
                <h2 className="font-semibold">Xu hướng tài chính</h2>
                <p className="text-xs text-slate-500">{report.fromDate} → {report.toDate}</p>
              </div>
              <Badge variant="success">Dữ liệu thật</Badge>
            </div>
            <div className="h-80">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={report.monthly}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Tooltip formatter={(value, name) => [formatMoney(Number(value)), name]} />
                  <Area type="monotone" dataKey="invoicedAmount" name="Hóa đơn" />
                  <Area type="monotone" dataKey="paidAmount" name="Đã thu" />
                  <Area type="monotone" dataKey="expenseAmount" name="Chi phí" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </Card>
        </>
      )}
    </div>
  );
}
