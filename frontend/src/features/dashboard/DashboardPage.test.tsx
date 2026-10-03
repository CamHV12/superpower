import { render, screen } from '@testing-library/react';
import { DashboardPage } from './DashboardPage';

describe('DashboardPage', () => {
  it('renders the enterprise overview and KPI metrics', () => {
    render(<DashboardPage />);

    expect(screen.getByRole('heading', { name: 'Tổng quan doanh nghiệp' })).toBeInTheDocument();
    expect(screen.getByText('Doanh thu tháng')).toBeInTheDocument();
    expect(screen.getByText('780 triệu')).toBeInTheDocument();
    expect(screen.getByText('Chi phí tháng')).toBeInTheDocument();
    expect(screen.getByText('Nhân sự')).toBeInTheDocument();
    expect(screen.getByText('Dự án đang chạy')).toBeInTheDocument();
  });

  it('shows recent activities and allows changing the reporting period', () => {
    render(<DashboardPage />);

    expect(screen.getByText('Hoạt động gần đây')).toBeInTheDocument();
    expect(screen.getByText('Nguyễn Minh Anh')).toBeInTheDocument();

    const select = screen.getByRole('combobox');
    expect(select).toHaveValue('6 tháng gần nhất');
  });
});
