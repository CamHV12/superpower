import { fireEvent, render, screen } from '@testing-library/react';
import { EmployeesPage } from './EmployeesPage';

describe('EmployeesPage', () => {
  it('renders employee management content', () => {
    render(<EmployeesPage />);
    expect(screen.getByRole('heading', { name: 'Nhân sự' })).toBeInTheDocument();
    expect(screen.getByText('Quản lý danh sách nhân viên')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Thêm nhân viên' })).toBeInTheDocument();
  });

  it('filters the employee list by search text', () => {
    render(<EmployeesPage />);
    fireEvent.change(screen.getByRole('textbox', { name: 'Tìm nhân viên' }), { target: { value: 'Nguyễn' } });
    expect(screen.getByText('Nguyễn Minh Anh')).toBeInTheDocument();
    expect(screen.queryByText('Trần Quốc Bảo')).not.toBeInTheDocument();
  });
});