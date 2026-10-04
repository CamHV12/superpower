import { useEffect, useState } from 'react';
import { Area, AreaChart, CartesianGrid, ResponsiveContainer, Tooltip, XAxis, YAxis } from 'recharts';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { reportsService, type ReportResponse } from './reports.service';

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
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = () => {
    setLoading(true);
    setError('');
    reportsService.summary(from, to)
      .then(setReport)
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
