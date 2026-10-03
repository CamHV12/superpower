import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { ArrowLeft, Pencil, UserPlus, Users } from 'lucide-react';
import { Link, useParams } from 'react-router-dom';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';
import { projectsService } from './projects.service';
import { TaskBoard } from './components/TaskBoard';
import type { Employee, Project, ProjectMember, Task } from './projects.types';

const statusLabels: Record<Project['status'], string> = {
  DRAFT: 'Nháp',
  ACTIVE: 'Đang chạy',
  ON_HOLD: 'Tạm dừng',
  COMPLETED: 'Hoàn thành',
  CANCELLED: 'Đã hủy',
};

const statusVariant = (status: Project['status']) => {
  if (status === 'ACTIVE' || status === 'COMPLETED') return 'success';
  if (status === 'ON_HOLD') return 'warning';
  if (status === 'CANCELLED') return 'danger';
  return 'neutral';
};

export function ProjectDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [project, setProject] = useState<Project | null>(null);
  const [members, setMembers] = useState<ProjectMember[]>([]);
  const [tasks, setTasks] = useState<Task[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedEmployee, setSelectedEmployee] = useState('');
  const [memberRole, setMemberRole] = useState('Member');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [memberError, setMemberError] = useState('');
  const [addingMember, setAddingMember] = useState(false);
  const [showEdit, setShowEdit] = useState(false);
  const [savingProject, setSavingProject] = useState(false);
  const [editError, setEditError] = useState('');
  const [editForm, setEditForm] = useState({
    name: '', description: '', managerId: '', startDate: '', endDate: '',
    budget: '0', status: 'DRAFT' as Project['status'], priority: 'MEDIUM' as Project['priority'], progress: 0,
  });

  const load = async () => {
    if (!id) return;
    setLoading(true);
    setError('');
    try {
      const [projectData, memberData, taskPage, employeePage] = await Promise.all([
        projectsService.get(id),
        projectsService.listMembers(id),
        projectsService.listTasks(id),
        projectsService.listEmployees(),
      ]);
      setProject(projectData);
      setEditForm({
        name: projectData.name,
        description: projectData.description || '',
        managerId: projectData.managerId,
        startDate: projectData.startDate,
        endDate: projectData.endDate,
        budget: String(projectData.budget),
        status: projectData.status,
        priority: projectData.priority,
        progress: projectData.progress,
      });
      setMembers(memberData);
      setTasks(taskPage.content);
      setEmployees(employeePage.content.filter((employee) => employee.active));
    } catch {
      setError('Không thể tải thông tin dự án.');
    } finally {
      setLoading(false);
    }
  };

  const reloadTasks = async () => {
    if (!id) return;
    const page = await projectsService.listTasks(id);
    setTasks(page.content);
  };

  useEffect(() => {
    void load();
  }, [id]);

  const saveProject = async (event: FormEvent) => {
    event.preventDefault();
    if (!id) return;
    setEditError('');
    if (editForm.startDate > editForm.endDate) {
      setEditError('Ngày bắt đầu không được sau ngày kết thúc.');
      return;
    }
    setSavingProject(true);
    try {
      const updated = await projectsService.update(id, {
        ...editForm,
        budget: Number(editForm.budget),
      });
      setProject(updated);
      setShowEdit(false);
    } catch {
      setEditError('Không thể cập nhật dự án. Vui lòng kiểm tra dữ liệu.');
    } finally {
      setSavingProject(false);
    }
  };

  const addMember = async () => {
    if (!id || !selectedEmployee) {
      setMemberError('Hãy chọn nhân viên.');
      return;
    }
    setAddingMember(true);
    setMemberError('');
    try {
      await projectsService.addMember(id, selectedEmployee, memberRole.trim() || 'Member');
      setMembers(await projectsService.listMembers(id));
      setSelectedEmployee('');
    } catch {
      setMemberError('Không thể thêm thành viên. Nhân viên có thể đã thuộc dự án.');
    } finally {
      setAddingMember(false);
    }
  };

  const removeMember = async (employeeId: string) => {
    if (!id) return;
    try {
      await projectsService.removeMember(id, employeeId);
      setMembers(await projectsService.listMembers(id));
    } catch {
      setMemberError('Không thể xóa thành viên.');
    }
  };

  if (loading) return <Card><p className="text-sm text-slate-500">Đang tải dự án...</p></Card>;
  if (error || !project) return <EmptyState title="Không tìm thấy dự án" description={error || 'Dự án không tồn tại.'} />;

  const availableEmployees = employees.filter((employee) => !members.some((member) => member.employeeId === employee.id));

  return (
    <div className="space-y-6">
      <Link to="/projects" className="inline-flex items-center gap-2 text-sm font-medium text-slate-500 hover:text-blue-600">
        <ArrowLeft className="size-4" /> Quay lại danh sách dự án
      </Link>

      <Card>
        <div className="flex flex-col justify-between gap-5 lg:flex-row lg:items-start">
          <div>
            <div className="flex flex-wrap items-center gap-2">
              <span className="text-xs font-semibold text-slate-500">{project.code}</span>
              <Badge variant={statusVariant(project.status)}>{statusLabels[project.status]}</Badge>
              <Badge variant="neutral">{project.priority}</Badge>
            </div>
            <h1 className="mt-2 text-2xl font-bold">{project.name}</h1>
            <Button variant="secondary" className="mt-3" onClick={() => setShowEdit((value) => !value)}><Pencil className="size-4" />Chỉnh sửa</Button>
            <p className="mt-2 max-w-3xl text-sm text-slate-500">{project.description || 'Chưa có mô tả.'}</p>
          </div>
          <div className="min-w-56">
            <div className="flex justify-between text-sm"><span>Tiến độ</span><strong>{project.progress}%</strong></div>
            <div className="mt-2 h-3 rounded-full bg-slate-100 dark:bg-slate-800"><div className="h-3 rounded-full bg-blue-600" style={{ width: `${project.progress}%` }} /></div>
            <p className="mt-2 text-xs text-slate-500">Quản lý: {project.managerName}</p>
          </div>
        </div>
        <div className="mt-6 grid gap-4 border-t border-slate-100 pt-5 sm:grid-cols-3 dark:border-slate-800">
          <div><p className="text-xs text-slate-500">Bắt đầu</p><p className="mt-1 font-medium">{project.startDate}</p></div>
          <div><p className="text-xs text-slate-500">Kết thúc</p><p className="mt-1 font-medium">{project.endDate}</p></div>
          <div><p className="text-xs text-slate-500">Ngân sách</p><p className="mt-1 font-medium">{new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND', maximumFractionDigits: 0 }).format(project.budget)}</p></div>
        </div>
      </Card>

      {showEdit && (
        <Card>
          <form onSubmit={saveProject} className="grid gap-4 md:grid-cols-2">
            <div><label className="text-sm font-medium">Tên dự án</label><input required value={editForm.name} onChange={(e) => setEditForm({ ...editForm, name: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Quản lý</label><select required value={editForm.managerId} onChange={(e) => setEditForm({ ...editForm, managerId: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">{employees.map((employee) => <option key={employee.id} value={employee.id}>{employee.fullName}</option>)}</select></div>
            <div><label className="text-sm font-medium">Trạng thái</label><select value={editForm.status} onChange={(e) => setEditForm({ ...editForm, status: e.target.value as Project['status'] })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">{Object.entries(statusLabels).map(([value, label]) => <option key={value} value={value}>{label}</option>)}</select></div>
            <div><label className="text-sm font-medium">Ưu tiên</label><select value={editForm.priority} onChange={(e) => setEditForm({ ...editForm, priority: e.target.value as Project['priority'] })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900"><option value="LOW">Thấp</option><option value="MEDIUM">Trung bình</option><option value="HIGH">Cao</option><option value="URGENT">Khẩn cấp</option></select></div>
            <div><label className="text-sm font-medium">Ngày bắt đầu</label><input required type="date" value={editForm.startDate} onChange={(e) => setEditForm({ ...editForm, startDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Ngày kết thúc</label><input required type="date" value={editForm.endDate} onChange={(e) => setEditForm({ ...editForm, endDate: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Ngân sách</label><input min="0" type="number" value={editForm.budget} onChange={(e) => setEditForm({ ...editForm, budget: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div><label className="text-sm font-medium">Tiến độ</label><input min="0" max="100" type="number" value={editForm.progress} onChange={(e) => setEditForm({ ...editForm, progress: Number(e.target.value) })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            <div className="md:col-span-2"><label className="text-sm font-medium">Mô tả</label><textarea rows={3} value={editForm.description} onChange={(e) => setEditForm({ ...editForm, description: e.target.value })} className="mt-1 w-full rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" /></div>
            {editError && <p className="md:col-span-2 text-sm text-red-600">{editError}</p>}
            <div className="md:col-span-2 flex justify-end gap-2"><Button type="button" variant="secondary" onClick={() => setShowEdit(false)}>Hủy</Button><Button type="submit" loading={savingProject}>Lưu thay đổi</Button></div>
          </form>
        </Card>
      )}

      <Card>
        <div className="flex items-center gap-2"><Users className="size-5 text-blue-600" /><h2 className="font-semibold">Thành viên dự án</h2></div>
        <div className="mt-4 grid gap-3 lg:grid-cols-[1fr_180px_auto]">
          <select aria-label="Chọn thành viên" value={selectedEmployee} onChange={(e) => setSelectedEmployee(e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900">
            <option value="">Chọn nhân viên</option>
            {availableEmployees.map((employee) => <option key={employee.id} value={employee.id}>{employee.fullName}</option>)}
          </select>
          <input aria-label="Vai trò thành viên" value={memberRole} onChange={(e) => setMemberRole(e.target.value)} className="rounded-xl border border-slate-200 px-3 py-2 text-sm dark:border-slate-700 dark:bg-slate-900" placeholder="Vai trò" />
          <Button onClick={() => void addMember()} loading={addingMember}><UserPlus className="size-4" />Thêm</Button>
        </div>
        {memberError && <p className="mt-2 text-sm text-red-600">{memberError}</p>}
        <div className="mt-5 divide-y divide-slate-100 dark:divide-slate-800">
          {members.map((member) => (
            <div key={member.id} className="flex items-center justify-between gap-3 py-3">
              <div><p className="font-medium">{member.employeeName}</p><p className="text-xs text-slate-500">{member.role}</p></div>
              <Button variant="ghost" onClick={() => void removeMember(member.employeeId)}>Xóa</Button>
            </div>
          ))}
          {members.length === 0 && <p className="py-4 text-sm text-slate-500">Chưa có thành viên.</p>}
        </div>
      </Card>

      <TaskBoard projectId={project.id} employees={employees} tasks={tasks} onReload={reloadTasks} />
    </div>
  );
}
