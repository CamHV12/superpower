import { render, screen } from '@testing-library/react';
import { vi } from 'vitest';
import { ErrorState } from './ErrorState';

describe('ErrorState', () => {
  it('renders the default error state', () => {
    render(<ErrorState />);

    expect(screen.getByRole('alert')).toBeInTheDocument();
    expect(screen.getByText('Không thể tải dữ liệu')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Thử lại' })).not.toBeInTheDocument();
  });

  it('calls retry when requested', async () => {
    const onRetry = vi.fn();
    render(<ErrorState onRetry={onRetry} />);

    await screen.getByRole('button', { name: 'Thử lại' }).click();
    expect(onRetry).toHaveBeenCalledOnce();
  });
});
