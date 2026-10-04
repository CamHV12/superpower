import { LogOut, Moon, Search, Sun } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { useUiStore } from '../../stores/ui.store';
import { useAuthStore } from '../../stores/auth.store';
import { NotificationBell } from './NotificationBell';

export function Header() {
  const theme = useUiStore((state) => state.theme);
  const toggleTheme = useUiStore((state) => state.toggleTheme);
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate('/login', { replace: true });
  }

  const fullName = user ? `${user.lastName} ${user.firstName}` : 'Người dùng';
  const role = user?.roles[0] ?? 'USER';

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-slate-200 bg-white/95 px-4 backdrop-blur dark:border-slate-800 dark:bg-slate-950/95 lg:px-6">
      <div className="relative hidden w-full max-w-md md:block">
        <Search className="absolute left-3 top-2.5 size-5 text-slate-400" />
        <input aria-label="Tìm kiếm" placeholder="Tìm kiếm..." className="w-full rounded-xl border border-slate-200 bg-slate-50 py-2 pl-10 pr-3 text-sm outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-900" />
      </div>
      <div className="ml-auto flex items-center gap-2">
        <NotificationBell />
        <button type="button" aria-label={theme === 'light' ? 'Bật chế độ tối' : 'Bật chế độ sáng'} onClick={toggleTheme} className="rounded-xl p-2 hover:bg-slate-100 dark:hover:bg-slate-800">
          {theme === 'light' ? <Moon className="size-5" /> : <Sun className="size-5" />}
        </button>
        <div className="ml-2 hidden text-right sm:block">
          <p className="text-sm font-semibold">{fullName}</p>
          <p className="text-xs text-slate-500">{role}</p>
        </div>
        <button type="button" aria-label="Đăng xuất" onClick={handleLogout} className="rounded-xl p-2 text-slate-500 hover:bg-slate-100 hover:text-red-600 dark:hover:bg-slate-800">
          <LogOut className="size-5" />
        </button>
      </div>
    </header>
  );
}
