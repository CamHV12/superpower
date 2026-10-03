import { Navigate, Route, Routes } from 'react-router-dom';
import { DashboardLayout } from '../layouts/DashboardLayout';
import { DashboardPage } from '../features/dashboard/DashboardPage';
import { LoginPage } from '../features/auth/LoginPage';
import { EmployeesPage } from '../features/employees/EmployeesPage';
import { ProtectedRoute } from './ProtectedRoute';
import { ProjectsPage } from '../features/projects/ProjectsPage';
import { CustomersPage } from '../features/customers/CustomersPage';
import { CustomerDetailPage } from '../features/customers/CustomerDetailPage';
import { ProjectDetailPage } from '../features/projects/ProjectDetailPage';

function Page({ title }: { title: string }) {
  return <section><h1 className="text-2xl font-bold">{title}</h1><p className="mt-2 text-slate-500">Module đang được xây dựng.</p></section>;
}

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      <Route element={<ProtectedRoute />}>
        <Route element={<DashboardLayout />}>
          <Route path="/" element={<DashboardPage />} />
          <Route path="/employees" element={<EmployeesPage />} />
          <Route path="/projects" element={<ProjectsPage />} />
          <Route path="/projects/:id" element={<ProjectDetailPage />} />
          <Route path="/customers" element={<CustomersPage />} />
          <Route path="/customers/:id" element={<CustomerDetailPage />} />
          <Route path="/finance" element={<Page title="Tài chính" />} />
          <Route path="/reports" element={<Page title="Báo cáo" />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
