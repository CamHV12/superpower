import { useMemo, useState } from 'react';
import { Plus, Search, UserRound } from 'lucide-react';
import { Badge } from '../../components/ui/Badge';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { EmptyState } from '../../components/ui/EmptyState';

const employees = [
  { name: 'Nguyễn Minh Anh', email: 'anh.nguyen@enterprise.vn', department: 'Kỹ thuật', position: 'Backend Developer', status: 'Đang làm việc' },
  { name: 'Trần Quốc Bảo', email: 'bao.tran@enterprise.vn', department: 'Kinh doanh', position: 'Sales Manager', status: 'Đang làm việc' },
  { name: 'Lê Thu Hà', email: 'ha.le@enterprise.vn', department: 'Tài chính', position: 'Accountant', status: 'Đang làm việc' },
  { name: 'Phạm Đức Long', email: 'long.pham@enterprise.vn', department: 'Kỹ thuật', position: 'Frontend Developer', status: 'Tạm nghỉ' },
];

export function EmployeesPage() {
  const [search, setSearch] = useState('');
  const filteredEmployees = useMemo(
    () => employees.filter((employee) => `${employee.name} ${employee.email} ${employee.department}`.toLowerCase().includes(search.toLowerCase())),
    [search],
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-bold">Nhân sự</h1>
          <p className="mt-1 text-sm text-slate-500">Quản lý danh sách nhân viên</p>
        </div>
        <Button><Plus className="size-4" />Thêm nhân viên</Button>
      </div>
      <Card>
        <div className="relative">
          <Search className="absolute left-3 top-2.5 size-4 text-slate-400" />
          <input aria-label="Tìm nhân viên" value={search} onChange={(event) => setSearch(event.target.value)} placeholder="Tìm theo tên, email hoặc phòng ban..." className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-9 pr-3 text-sm outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" />
        </div>
      </Card>
      {filteredEmployees.length === 0 ? (
        <EmptyState title="Không tìm thấy nhân viên" description="Thử thay đổi từ khóa tìm kiếm." />
      ) : (
        <Card className="overflow-hidden p-0">
          <div className="overflow-x-auto">
            <table className="w-full min-w-[760px] text-left text-sm">
              <thead className="bg-slate-50 dark:bg-slate-900">
                <tr><th className="px-5 py-3">Nhân viên</th><th className="px-5 py-3">Phòng ban</th><th className="px-5 py-3">Chức vụ</th><th className="px-5 py-3">Trạng thái</th></tr>
              </thead>
              <tbody className="divide-y divide-slate-100 dark:divide-slate-800">
                {filteredEmployees.map((employee) => (
                  <tr key={employee.email}>
                    <td className="px-5 py-4"><div className="flex items-center gap-3"><div className="rounded-full bg-blue-50 p-2 text-blue-600"><UserRound className="size-4" /></div><div><p className="font-medium">{employee.name}</p><p className="text-xs text-slate-500">{employee.email}</p></div></div></td>
                    <td className="px-5 py-4">{employee.department}</td><td className="px-5 py-4">{employee.position}</td>
                    <td className="px-5 py-4"><Badge variant={employee.status === 'Đang làm việc' ? 'success' : 'warning'}>{employee.status}</Badge></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Card>
      )}
    </div>
  );
}