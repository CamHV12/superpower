import { useEffect, useState } from 'react';
import { Badge } from '../../components/ui/Badge';
import { Card } from '../../components/ui/Card';
import { auditService, type AuditLog } from './audit.service';

export function AuditPage() {
  const [items, setItems] = useState<AuditLog[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    setError('');
    auditService.list(page)
      .then(data => {
        setItems(data.content);
        setTotalPages(data.totalPages);
      })
      .catch(() => setError('Không thể tải nhật ký hoạt động.'))
      .finally(() => setLoading(false));
  }, [page]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold tracking-tight">Nhật ký hoạt động</h1>
        <p className="mt-1 text-sm text-slate-500">Theo dõi các request API và trạng thái xử lý của hệ thống.</p>
      </div>

      <Card>
        {loading ? (
          <p className="text-sm text-slate-500">Đang tải nhật ký...</p>
        ) : error ? (
          <p className="text-sm text-red-600">{error}</p>
        ) : items.length === 0 ? (
          <p className="py-8 text-center text-sm text-slate-500">Chưa có hoạt động nào.</p>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full min-w-[900px] text-sm">
              <thead>
                <tr className="border-b border-slate-200 text-left dark:border-slate-700">
                  <th className="px-3 py-3">Thời gian</th>
                  <th className="px-3 py-3">Người dùng</th>
                  <th className="px-3 py-3">Method</th>
                  <th className="px-3 py-3">API</th>
                  <th className="px-3 py-3">Status</th>
                  <th className="px-3 py-3">Thời gian xử lý</th>
                </tr>
              </thead>
              <tbody>
                {items.map(item => (
                  <tr key={item.id} className="border-b border-slate-100 dark:border-slate-800">
                    <td className="px-3 py-3 whitespace-nowrap">{new Date(item.createdAt).toLocaleString('vi-VN')}</td>
                    <td className="px-3 py-3">{item.actorEmail ?? 'Anonymous'}</td>
                    <td className="px-3 py-3"><Badge variant={item.action === 'GET' ? 'info' : 'success'}>{item.action}</Badge></td>
                    <td className="max-w-[360px] truncate px-3 py-3" title={item.path}>{item.path}</td>
                    <td className="px-3 py-3">{item.statusCode}</td>
                    <td className="px-3 py-3">{item.durationMs} ms</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {!loading && !error && totalPages > 1 && (
          <div className="mt-4 flex items-center justify-between border-t border-slate-200 pt-4 text-sm dark:border-slate-700">
            <span>Trang {page + 1} / {totalPages}</span>
            <div className="flex gap-2">
              <button type="button" disabled={page === 0} onClick={() => setPage(current => current - 1)} className="rounded-lg border px-3 py-2 disabled:opacity-40">Trước</button>
              <button type="button" disabled={page >= totalPages - 1} onClick={() => setPage(current => current + 1)} className="rounded-lg border px-3 py-2 disabled:opacity-40">Sau</button>
            </div>
          </div>
        )}
      </Card>
    </div>
  );
}