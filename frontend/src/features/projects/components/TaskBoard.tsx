import { useState } from 'react';
import type { FormEvent } from 'react';
import { Plus, RefreshCw } from 'lucide-react';
import { Badge } from '../../../components/ui/Badge';
import { Button } from '../../../components/ui/Button';
import { Card } from '../../../components/ui/Card';
import { projectsService } from '../projects.service';
import type { Employee, Task } from '../projects.types';

const columns: Array<{ status: Task['status']; label: string }> = [
  { status: 'TODO', label: 'Cần làm' },
  { status: 'IN_PROGRESS', label: 'Đang làm' },
  { status: 'REVIEW', label: 'Chờ review' },
  { status: 'DONE', label: 'Hoàn thành' },
];

const priorityVariant = (priority: Task['priority']) => {
  if (priority === 'URGENT' || priority === 'HIGH') return 'danger';
  if (priority === 'MEDIUM') return 'warning';
  return 'neutral';
};

interface TaskBoardProps {
  projectId: string;
  employees: Employee[];
  tasks: Task[];
  onReload: () => Promise<void>;
}

export function TaskBoard({ projectId, employees, tasks, onReload }: TaskBoardProps) {
  const [showForm, setShowForm] = useState(false);
  const [saving, setSaving] = useState(false);
  const [formError, setFormError] = useState('');
  const [form, setForm] = useState({
    code: '',
    title: '',
    description: '',
    assigneeId: '',
    startDate: '',
    dueDate: '',
    estimatedHours: '0',
    priority: 'MEDIUM' as Task['priority'],
  });

  const createTask = async (event: FormEvent) => {
    event.preventDefault();
    setFormError('');
    if (!form.assigneeId || !form.startDate || !form.dueDate) {
      setFormError('Vui lòng nhập người thực hiện và thời gian.');
      return;
    }
    if (form.startDate > form.dueDate) {
      setFormError('Ngày bắt đầu không được sau ngày đến hạn.');
      return;
    }

    setSaving(true);
    try {
      await projectsService.createTask(projectId, {
        ...form,
        estimatedHours: Number(form.estimatedHours),
      });
      setShowForm(false);
      setForm({
        code: '',
        title: '',
        description: '',
        assigneeId: '',
        startDate: '',
        dueDate: '',
        estimatedHours: '0',
        priority: 'MEDIUM',
      });
      await onReload();
    } catch {
      setFormError('Không thể tạo công việc. Kiểm tra mã công việc và dữ liệu.');
    } finally {
      setSaving(false);
    }
  };

  const updateProgress = async (task: Task, progress: number) => {
    try {
      await projectsService.updateTaskProgress(projectId, task.id, progress);
      await onReload();
    } catch {
      setFormError('Không thể cập nhật tiến độ công việc.');
    }
  };

  return (
    <section className="space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="font-semibold">Công việc</h2>
          <p className="text-xs text-slate-500">{tasks.length} công việc trong dự án</p>
        </div>
        <div className="flex gap-2">
          <Button variant="secondary" onClick={() => void onReload()}><RefreshCw className="size-4" />Làm mới</Button>
          <Button onClick={() => setShowForm((value) => !value)}><Plus className="size-4" />Tạo task</Button>
        </div>
      </div>

      {showForm && (
        <Card>
          <form onSubmit={createTask} className="grid gap-4 md:grid-cols-2">
            <div><label className="text-sm font-medium">Mã task</label><input required value={form.code} onChange={(e) => setForm({ ...form, code: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" placeholder="TASK-001" /></div>
            <div><label className="text-sm font-medium">Tiêu đề</label><input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Người thực hiện</label><select required value={form.assigneeId} onChange={(e) => setForm({ ...form, assigneeId: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"><option value="">Chọn nhân viên</option>{employees.filter((e) => e.active).map((employee) => <option key={employee.id} value={employee.id}>{employee.fullName}</option>)}</select></div>
            <div><label className="text-sm font-medium">Ưu tiên</label><select value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value as Task['priority'] })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"><option value="LOW">Thấp</option><option value="MEDIUM">Trung bình</option><option value="HIGH">Cao</option><option value="URGENT">Khẩn cấp</option></select></div>
            <div><label className="text-sm font-medium">Ngày bắt đầu</label><input required type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Hạn hoàn thành</label><input required type="date" value={form.dueDate} onChange={(e) => setForm({ ...form, dueDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Giờ dự kiến</label><input min="0" step="0.5" type="number" value={form.estimatedHours} onChange={(e) => setForm({ ...form, estimatedHours: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div className="md:col-span-2"><label className="text-sm font-medium">Mô tả</label><textarea rows={2} value={form.description} onChange={(e) => setForm({ ...form, description: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            {formError && <p className="md:col-span-2 text-sm text-red-600">{formError}</p>}
            <div className="md:col-span-2 flex justify-end"><Button type="submit" loading={saving}>Lưu task</Button></div>
          </form>
        </Card>
      )}

      <div className="grid gap-4 xl:grid-cols-4">
        {columns.map((column) => {
          const columnTasks = tasks.filter((task) => task.status === column.status);
          return (
            <Card key={column.status} className="min-h-64 bg-slate-50/70 dark:bg-slate-900/50">
              <div className="mb-3 flex items-center justify-between">
                <h3 className="text-sm font-semibold">{column.label}</h3>
                <span className="rounded-full bg-white px-2 py-1 text-xs text-slate-500 dark:bg-slate-950">{columnTasks.length}</span>
              </div>
              <div className="space-y-3">
                {columnTasks.map((task) => (
                  <div key={task.id} className="rounded-xl border border-slate-200 bg-white p-3 shadow-sm dark:border-slate-800 dark:bg-slate-950">
                    <div className="flex items-start justify-between gap-2">
                      <div><p className="text-xs text-slate-500">{task.code}</p><p className="mt-1 text-sm font-semibold">{task.title}</p></div>
                      <Badge variant={priorityVariant(task.priority)}>{task.priority}</Badge>
                    </div>
                    <p className="mt-2 text-xs text-slate-500">👤 {task.assigneeName}</p>
                    <div className="mt-3">
                      <div className="mb-1 flex justify-between text-xs text-slate-500"><span>Tiến độ</span><span>{task.progress}%</span></div>
                      <input aria-label={`Tiến độ ${task.title}`} type="range" min="0" max="100" value={task.progress} onChange={(e) => void updateProgress(task, Number(e.target.value))} className="w-full" />
                    </div>
                  </div>
                ))}
                {columnTasks.length === 0 && <p className="rounded-xl border border-dashed border-slate-300 p-4 text-center text-xs text-slate-400 dark:border-slate-700">Chưa có task</p>}
              </div>
            </Card>
          );
        })}
      </div>
      {formError && !showForm && <p className="text-sm text-red-600">{formError}</p>}
    </section>
  );
}
