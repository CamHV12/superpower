import { useEffect, useState } from 'react';
import { ArrowLeft, UserPlus, Users } from 'lucide-react';
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
