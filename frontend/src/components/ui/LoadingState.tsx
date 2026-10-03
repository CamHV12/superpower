interface LoadingStateProps {
  rows?: number;
}

export function LoadingState({ rows = 3 }: LoadingStateProps) {
  return (
    <div aria-label="Đang tải" className="space-y-3">
      {Array.from({ length: rows }, (_, index) => <div key={index} className="h-12 animate-pulse rounded-xl bg-slate-200 dark:bg-slate-800" />)}
    </div>
  );
}
