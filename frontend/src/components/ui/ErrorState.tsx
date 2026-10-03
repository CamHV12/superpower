import { AlertCircle, RefreshCw } from 'lucide-react';
import { Button } from './Button';

interface ErrorStateProps {
  title?: string;
  description?: string;
  onRetry?: () => void;
}

export function ErrorState({
  title = 'Không thể tải dữ liệu',
  description = 'Đã xảy ra lỗi khi tải dữ liệu. Vui lòng thử lại.',
  onRetry,
}: ErrorStateProps) {
  return (
    <div role="alert" className="flex flex-col items-center justify-center rounded-2xl border border-red-200 bg-red-50 px-6 py-10 text-center dark:border-red-900/50 dark:bg-red-950/20">
      <AlertCircle className="size-8 text-red-500" />
      <h2 className="mt-3 font-semibold text-slate-900 dark:text-white">{title}</h2>
      <p className="mt-1 max-w-md text-sm text-slate-500 dark:text-slate-400">{description}</p>
      {onRetry && (
        <Button variant="secondary" className="mt-4" onClick={onRetry}>
          <RefreshCw className="size-4" />
          Thử lại
        </Button>
      )}
    </div>
  );
}
