import { Outlet } from 'react-router-dom';
import { useState } from 'react';
import { Header } from '../components/layout/Header';
import { Sidebar } from '../components/layout/Sidebar';

export function DashboardLayout() {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <div className="min-h-screen bg-slate-50 text-slate-900 dark:bg-slate-900 dark:text-slate-100">
      <Sidebar collapsed={collapsed} onToggle={() => setCollapsed((value) => !value)} />
      <div className={`min-h-screen transition-[margin] ${collapsed ? 'ml-20' : 'ml-64'}`}>
        <Header />
        <main className="p-4 lg:p-6"><Outlet /></main>
      </div>
    </div>
  );
}
