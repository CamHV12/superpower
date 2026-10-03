import { Bell, Moon, Search, Sun } from 'lucide-react';
import { useUiStore } from '../../stores/ui.store';

export function Header() {
  const theme = useUiStore((state) => state.theme);
  const toggleTheme = useUiStore((state) => state.toggleTheme);

  return (
    <header className="sticky top-0 z-30 flex h-16 items-center justify-between border-b border-slate-200 bg-white/95 px-4 backdrop-blur dark:border-slate-800 dark:bg-slate-950/95 lg:px-6">
      <div className="relative hidden w-full max-w-md md:block">
        <Search className="absolute left-3 top-2.5 size-5 text-slate-400" />
        <input aria-label="Tìm kiếm" placeholder="Tìm kiếm..." className="w-full rounded-xl border border-slate-200 bg-slate-50 py-2 pl-10 pr-3 text-sm outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-900" />
      </div>
      <div className="ml-auto flex items-center gap-2">
        <button type="button" aria-label="Thông báo" className="rounded-xl p-2 hover:bg-slate-100 dark:hover:bg-slate-800"><Bell className="size-5" /></button>
        <button type="button" aria-label={theme === 'light' ? 'Bật chế độ tối' : 'Bật chế độ sáng'} onClick={toggleTheme} className="rounded-xl p-2 hover:bg-slate-100 dark:hover:bg-slate-800">
          {theme === 'light' ? <Moon className="size-5" /> : <Sun className="size-5" />}
        </button>
        <div className="ml-2 hidden text-right sm:block">
          <p className="text-sm font-semibold">Nguyễn Văn An</p>
          <p className="text-xs text-slate-500">Administrator</p>
        </div>
      </div>
    </header>
  );
}
