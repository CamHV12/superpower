import { BriefcaseBusiness, ChevronLeft, CircleDollarSign, FileBarChart, LayoutDashboard, Users } from 'lucide-react';
import { NavLink } from 'react-router-dom';

const navigation = [
  { label: 'Tổng quan', to: '/', icon: LayoutDashboard },
  { label: 'Nhân sự', to: '/employees', icon: Users },
  { label: 'Dự án', to: '/projects', icon: BriefcaseBusiness },
  { label: 'Tài chính', to: '/finance', icon: CircleDollarSign },
  { label: 'Khách hàng', to: '/customers', icon: Users },
  { label: 'Báo cáo', to: '/reports', icon: FileBarChart },
];

interface SidebarProps {
  collapsed: boolean;
  onToggle: () => void;
}

export function Sidebar({ collapsed, onToggle }: SidebarProps) {
  return (
    <aside className={`fixed inset-y-0 left-0 z-40 border-r border-slate-200 bg-white transition-all dark:border-slate-800 dark:bg-slate-950 ${collapsed ? 'w-20' : 'w-64'}`}>
      <div className="flex h-16 items-center justify-between border-b border-slate-200 px-4 dark:border-slate-800">
        {!collapsed && <span className="font-bold text-slate-900 dark:text-white">Enterprise Hub</span>}
        <button type="button" onClick={onToggle} aria-label="Thu gọn menu" className="rounded-lg p-2 hover:bg-slate-100 dark:hover:bg-slate-800">
          <ChevronLeft className={`size-5 transition-transform ${collapsed ? 'rotate-180' : ''}`} />
        </button>
      </div>
      <nav className="space-y-1 p-3">
        {navigation.map(({ label, to, icon: Icon }) => (
          <NavLink key={to} to={to} end={to === '/'} className={({ isActive }) => `flex items-center gap-3 rounded-xl px-3 py-2.5 text-sm font-medium transition-colors ${isActive ? 'bg-blue-50 text-blue-700 dark:bg-blue-950/50 dark:text-blue-300' : 'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800'}`}>
            <Icon className="size-5 shrink-0" />
            {!collapsed && <span>{label}</span>}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
