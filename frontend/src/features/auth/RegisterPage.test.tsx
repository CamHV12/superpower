import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { RegisterPage } from './RegisterPage';

const registerMock = vi.fn();

vi.mock('../../services/auth.service', () => ({
  register: (...args: unknown[]) => registerMock(...args),
}));

describe('RegisterPage', () => {
  beforeEach(() => registerMock.mockReset());

  it('rejects mismatched passwords before calling the API', async () => {
    render(<MemoryRouter><RegisterPage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText('Họ'), { target: { value: 'Nguyen' } });
    fireEvent.change(screen.getByLabelText('Tên'), { target: { value: 'An' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'an@example.com' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu.*72 ký tự/), { target: { value: 'StrongPass123' } });
    fireEvent.change(screen.getByLabelText('Xác nhận mật khẩu'), { target: { value: 'DifferentPass123' } });
    fireEvent.click(screen.getByRole('button', { name: 'Đăng ký' }));

    expect(await screen.findByRole('alert')).toHaveTextContent('Mật khẩu xác nhận không khớp.');
    expect(registerMock).not.toHaveBeenCalled();
  });

  it('submits the registration details', async () => {
    registerMock.mockResolvedValue({
      accessToken: 'jwt-token',
      tokenType: 'Bearer',
      expiresIn: 3600,
      refreshToken: 'refresh-token',
      user: {
        id: 'user-1',
        email: 'an@example.com',
        firstName: 'An',
        lastName: 'Nguyen',
        roles: ['EMPLOYEE'],
      },
    });

    render(<MemoryRouter><RegisterPage /></MemoryRouter>);
    fireEvent.change(screen.getByLabelText('Họ'), { target: { value: ' Nguyen ' } });
    fireEvent.change(screen.getByLabelText('Tên'), { target: { value: ' An ' } });
    fireEvent.change(screen.getByLabelText('Email'), { target: { value: ' an@example.com ' } });
    fireEvent.change(screen.getByLabelText(/Mật khẩu.*72 ký tự/), { target: { value: 'StrongPass123' } });
    fireEvent.change(screen.getByLabelText('Xác nhận mật khẩu'), { target: { value: 'StrongPass123' } });
    fireEvent.click(screen.getByRole('button', { name: 'Đăng ký' }));

    await waitFor(() => expect(registerMock).toHaveBeenCalledWith({
      firstName: 'An',
      lastName: 'Nguyen',
      email: 'an@example.com',
      password: 'StrongPass123',
    }));
  });
});
