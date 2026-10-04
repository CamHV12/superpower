import { Bell, CheckCheck } from 'lucide-react';
import { useEffect, useState } from 'react';
import { notificationService, type NotificationItem } from '../../features/notifications/notification.service';

export function NotificationBell() {
  const [unreadCount, setUnreadCount] = useState(0);
  const [items, setItems] = useState<NotificationItem[]>([]);
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);

  const load = async () => {
    try {
      const [count, page] = await Promise.all([
        notificationService.unreadCount(),
        notificationService.list(false, 0, 8),
      ]);
      setUnreadCount(count);
      setItems(page.content);
    } catch {
      // Notifications are optional UI and must not block the application shell.
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const markRead = async (id: string) => {
    try {
      await notificationService.markRead(id);
      setItems(current => current.map(item => item.id === id ? { ...item, read: true } : item));
      setUnreadCount(current => Math.max(0, current - 1));
    } catch {
      // Keep the current state when the server rejects the update.
    }
  };

  const markAllRead = async () => {
    setLoading(true);
    try {
      await notificationService.markAllRead();
      setItems(current => current.map(item => ({ ...item, read: true })));
      setUnreadCount(0);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="relative">
      <button
        type="button"
        aria-label="Thông báo"
        onClick={() => setOpen(current => !current)}
        className="relative rounded-xl p-2 hover:bg-slate-100 dark:hover:bg-slate-800"
      >
        <Bell className="size-5" />
        {unreadCount > 0 && (
          <span className="absolute -right-0.5 -top-0.5 min-w-4 rounded-full bg-red-500 px-1 text-center text-[10px] font-bold text-white">
            {unreadCount > 99 ? '99+' : unreadCount}
          </span>
        )}
      </button>

      {open && (
        <div className="absolute right-0 top-11 z-50 w-80 overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-xl dark:border-slate-700 dark:bg-slate-900">
          <div className="flex items-center justify-between border-b border-slate-200 px-4 py-3 dark:border-slate-700">
            <div>
              <p className="font-semibold">Thông báo</p>
              <p className="text-xs text-slate-500">{unreadCount} chưa đọc</p>
            </div>
            <button type="button" aria-label="Đánh dấu tất cả đã đọc" onClick={() => void markAllRead()} disabled={loading || unreadCount === 0} className="rounded-lg p-2 text-slate-500 hover:bg-slate-100 disabled:opacity-40 dark:hover:bg-slate-800">
              <CheckCheck className="size-4" />
            </button>
          </div>
          <div className="max-h-96 overflow-y-auto">
            {items.length === 0 ? (
              <p className="px-4 py-8 text-center text-sm text-slate-500">Chưa có thông báo.</p>
            ) : items.map(item => (
              <button
                type="button"
                key={item.id}
                onClick={() => !item.read && void markRead(item.id)}
                className="block w-full border-b border-slate-100 px-4 py-3 text-left hover:bg-slate-50 dark:border-slate-800 dark:hover:bg-slate-800/60"
              >
                <div className="flex items-start gap-2">
                  {!item.read && <span className="mt-1.5 size-2 shrink-0 rounded-full bg-blue-500" />}
                  <div className={item.read ? 'pl-4' : ''}>
                    <p className="text-sm font-semibold">{item.title}</p>
                    <p className="mt-1 text-xs text-slate-500">{item.message}</p>
                    <p className="mt-2 text-[11px] text-slate-400">{new Date(item.createdAt).toLocaleString('vi-VN')}</p>
                  </div>
                </div>
              </button>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}