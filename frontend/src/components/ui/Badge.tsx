import type { HTMLAttributes, ReactNode } from 'react';

type BadgeVariant = 'success' | 'warning' | 'danger' | 'info' | 'neutral';

interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant;
  children: ReactNode;
}

const variants: Record<BadgeVariant, string> = {
  success: 'bg-emerald-50 text-emerald-700 dark:bg-emerald-950/40 dark:text-emerald-300',
  warning: 'bg-amber-50 text-amber-700 dark:bg-amber-950/40 dark:text-amber-300',
  danger: 'bg-red-50 text-red-700 dark:bg-red-950/40 dark:text-red-300',
  info: 'bg-blue-50 text-blue-700 dark:bg-blue-950/40 dark:text-blue-300',
  neutral: 'bg-slate-100 text-slate-700 dark:bg-slate-800 dark:text-slate-300',
};

export function Badge({ variant = 'neutral', children, className = '', ...props }: BadgeProps) {
  return <span {...props} className={`inline-flex items-center rounded-full px-2.5 py-1 text-xs font-semibold ${variants[variant]} ${className}`}>{children}</span>;
}
