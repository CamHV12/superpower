import { Navigate, Route, Routes } from 'react-router-dom';
import { DashboardLayout } from '../layouts/DashboardLayout';
import { DashboardPage } from '../features/dashboard/DashboardPage';
import { LoginPage } from '../features/auth/LoginPage';
import { ProtectedRoute } from './ProtectedRoute';

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
          <Route path="/employees" element={<Page title="Nhân sự" />} />
          <Route path="/projects" element={<Page title="Dự án" />} />
          <Route path="/finance" element={<Page title="Tài chính" />} />
          <Route path="/customers" element={<Page title="Khách hàng" />} />
          <Route path="/reports" element={<Page title="Báo cáo" />} />
        </Route>
      </Route>

      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
