import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { BriefcaseBusiness, Plus, Search, SlidersHorizontal } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import { projectsService } from './projects.service';
import type { Employee, Project, ProjectPriority, ProjectStatus } from './projects.types';

const statusLabels: Record<ProjectStatus, string> = {
  DRAFT: 'Nháp',
  ACTIVE: 'Đang chạy',
  ON_HOLD: 'Tạm dừng',
  COMPLETED: 'Hoàn thành',
  CANCELLED: 'Đã hủy',
};

const priorityLabels: Record<ProjectPriority, string> = {
  LOW: 'Thấp',
  MEDIUM: 'Trung bình',
  HIGH: 'Cao',
  URGENT: 'Khẩn cấp',
};

const statusVariant = (status: ProjectStatus) => {
  if (status === 'ACTIVE' || status === 'COMPLETED') return 'success';
  if (status === 'ON_HOLD') return 'warning';
  if (status === 'CANCELLED') return 'danger';
  return 'neutral';
};

const money = (value: number) =>
  new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(value);

export function ProjectsPage() {
  const [projects, setProjects] = useState<Project[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [customers, setCustomers] = useState<{ id: string; name: string }[]>([]);
  const [customerId, setCustomerId] = useState('');
  const [keyword, setKeyword] = useState('');
  const [status, setStatus] = useState<ProjectStatus | ''>('');
  const [priority, setPriority] = useState<ProjectPriority | ''>('');
  const [managerId, setManagerId] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [formError, setFormError] = useState('');
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    code: '',
    name: '',
    description: '',
    managerId: '',
    startDate: '',
    endDate: '',
    budget: '0',
    priority: 'MEDIUM' as ProjectPriority,
    customerId: '',
  });

  const load = async () => {
    setLoading(true);
    setError('');
    try {
      const [projectPage, employeePage, customerPage] = await Promise.all([
        projectsService.list({
          page,
          size: 10,
          keyword,
          status: status || undefined,
          priority: priority || undefined,
          managerId: managerId || undefined,
          customerId: customerId || undefined,
        }),
        projectsService.listEmployees(),
        projectsService.listCustomers(),
      ]);
      setProjects(projectPage.content);
      setTotalPages(projectPage.totalPages);
      setEmployees(employeePage.content.filter((employee) => employee.active));
      setCustomers(customerPage.content);
    } catch {
      setError('Không thể tải danh sách dự án. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    setPage(0);
  }, [keyword, status, priority, managerId, customerId]);

  useEffect(() => {
    void load();
  }, [keyword, status, priority, managerId, page]);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setFormError('');
    if (!form.managerId || !form.startDate || !form.endDate) {
      setFormError('Vui lòng nhập người quản lý và khoảng thời gian dự án.');
      return;
    }
    if (form.startDate > form.endDate) {
      setFormError('Ngày bắt đầu không được sau ngày kết thúc.');
      return;
    }

    setSaving(true);
    try {
      await projectsService.create({
        ...form,
        budget: Number(form.budget),
        customerId: form.customerId || undefined,
      });
      setShowForm(false);
      setForm({
        code: '',
        name: '',
        description: '',
        managerId: '',
        customerId: '',
        startDate: '',
        endDate: '',
        budget: '0',
        priority: 'MEDIUM',
      });
      await load();
    } catch {
      setFormError('Không thể tạo dự án. Kiểm tra mã dự án hoặc dữ liệu nhập.');
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold tracking-tight">Dự án</h1>
          <p className="mt-1 text-sm text-slate-500">Quản lý dự án, tiến độ và công việc theo dự án.</p>
        </div>
        <Button onClick={() => setShowForm((value) => !value)}>
          <Plus className="size-4" />
          {showForm ? 'Đóng biểu mẫu' : 'Tạo dự án'}
        </Button>
      </div>

      {showForm && (
        <Card>
          <form onSubmit={submit} className="grid gap-4 md:grid-cols-2">
            <div>
              <label className="text-sm font-medium">Mã dự án</label>
              <input required value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" placeholder="PRJ-001" />
            </div>
            <div>
              <label className="text-sm font-medium">Tên dự án</label>
              <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" placeholder="Website doanh nghiệp" />
            </div>
            <div className="md:col-span-2">
              <label className="text-sm font-medium">Mô tả</label>
              <textarea value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} rows={3} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            <div>
              <label className="text-sm font-medium">Quản lý dự án</label>
              <select required value={form.managerId} onChange={(e) => setForm({ ...form, managerId: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
                <option value="">Chọn nhân viên</option>
                {employees.map((employee) => <option key={employee.id} value={employee.id}>{employee.fullName}</option>)}
              </select>
            </div>
            <div>
              <label className="text-sm font-medium">Khách hàng</label>
              <select value={form.customerId} onChange={(e) => setForm({ ...form, customerId: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
                <option value="">Chưa gán khách hàng</option>
                {customers.map((customer) => <option key={customer.id} value={customer.id}>{customer.name}</option>)}
              </select>
            </div>
            <div>
              <label className="text-sm font-medium">Ưu tiên</label>
              <select value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value as ProjectPriority })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
                {Object.entries(priorityLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
              </select>
            </div>
            <div>
              <label className="text-sm font-medium">Ngày bắt đầu</label>
              <input required type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            <div>
              <label className="text-sm font-medium">Ngày kết thúc</label>
              <input required type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            <div>
              <label className="text-sm font-medium">Ngân sách</label>
              <input required min="0" type="number" value={form.budget} onChange={(e) => setForm({ ...form, budget: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" />
            </div>
            {formError && <p className="md:col-span-2 text-sm text-red-600">{formError}</p>}
            <div className="md:col-span-2 flex justify-end">
              <Button type="submit" loading={saving}>Lưu dự án</Button>
            </div>
          </form>
        </Card>
      )}

      <Card>
        <div className="grid gap-3 lg:grid-cols-[1fr_auto_auto_auto]">
          <div className="relative">
            <Search className="absolute left-3 top-2.5 size-4 text-slate-400" />
            <input aria-label="Tìm dự án" value={keyword} onChange={(e) => setKeyword(e.target.value)} placeholder="Tìm theo mã, tên hoặc mô tả..." className="w-full rounded-xl border border-slate-200 py-2.5 pl-9 pr-3 text-sm outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-900" />
          </div>
          <select aria-label="Lọc trạng thái" value={status} onChange={(e) => setStatus(e.target.value as ProjectStatus | '')} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Tất cả trạng thái</option>
            {Object.entries(statusLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
          <select aria-label="Lọc ưu tiên" value={priority} onChange={(e) => setPriority(e.target.value as ProjectPriority | '')} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Tất cả ưu tiên</option>
            {Object.entries(priorityLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}
          </select>
          <select aria-label="Lọc khách hàng" value={customerId} onChange={(e) => setCustomerId(e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Tất cả khách hàng</option>
            {customers.map((customer) => <option key={customer.id} value={customer.id}>{customer.name}</option>)}
          </select>
          <select aria-label="Lọc quản lý" value={managerId} onChange={(e) => setManagerId(e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Tất cả quản lý</option>
            {employees.map((employee) => <option key={employee.id} value={employee.id}>{employee.fullName}</option>)}
          </select>
        </div>
      </Card>

      {error ? (
        <Card>
          <div className="flex items-center gap-3 text-sm text-red-600"><SlidersHorizontal className="size-4" />{error}<Button variant="secondary" onClick={() => void load()}>Thử lại</Button></div>
        </Card>
      ) : loading ? (
        <Card><p className="text-sm text-slate-500">Đang tải dự án...</p></Card>
      ) : projects.length === 0 ? (
        <EmptyState title="Chưa có dự án" description="Tạo dự án đầu tiên để bắt đầu quản lý công việc." />
      ) : (
        <Card className="overflow-hidden p-0">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[900px] text-left text-sm">
              <thead className="bg-slate-50 dark:bg-slate-900">
                <tr>
                  <th className="px-5 py-3">Dự án</th>
                  <th className="px-5 py-3">Khách hàng</th>
                  <th className="px-5 py-3">Quản lý</th>
                  <th className="px-5 py-3">Trạng thái</th>
                  <th className="px-5 py-3">Ưu tiên</th>
                  <th className="px-5 py-3">Tiến độ</th>
                  <th className="px-5 py-3">Ngân sách</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {projects.map((project) => (
                  <tr key={project.id} className="hover:bg-slate-50 dark:hover:bg-slate-900/50">
                    <td className="px-5 py-4">
                      <Link to={`/projects/${project.id}`} className="flex items-center gap-3">
                        <div className="rounded-xl bg-blue-50 p-2 text-blue-600 dark:bg-blue-950/40"><BriefcaseBusiness className="size-4" /></div>
                        <div><p className="font-semibold hover:text-blue-600">{project.name}</p><p className="text-xs text-slate-500">{project.code}</p></div>
                      </Link>
                    </td>
                    <td className="px-5 py-4">{project.customerName || '—'}</td>
                    <td className="px-5 py-4">{project.managerName}</td>
                    <td className="px-5 py-4"><Badge variant={statusVariant(project.status)}>{statusLabels[project.status]}</Badge></td>
                    <td className="px-5 py-4">{priorityLabels[project.priority]}</td>
                    <td className="px-5 py-4">
                      <div className="min-w-32">
                        <div className="mb-1 flex justify-between text-xs"><span>{project.progress}%</span></div>
                        <div className="h-2 rounded-full bg-slate-100 dark:bg-slate-800"><div className="h-2 rounded-full bg-blue-600" style={{ width: `${project.progress}%` }} /></div>
                      </div>
                    </td>
                    <td className="px-5 py-4">{money(project.budget)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {totalPages > 1 && (
            <div className="flex items-center justify-between border-t border-slate-100 px-5 py-3 text-sm dark:border-slate-800">
              <span className="text-slate-500">Trang {page + 1} / {totalPages}</span>
              <div className="flex gap-2">
                <Button variant="secondary" disabled={page === 0} onClick={() => setPage((value) => value - 1)}>Trước</Button>
                <Button variant="secondary" disabled={page >= totalPages - 1} onClick={() => setPage((value) => value + 1)}>Sau</Button>
              </div>
            </div>
          )}
        </Card>
      )}
    </div>
  );
}
