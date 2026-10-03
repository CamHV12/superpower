import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { vi } from 'vitest';
import { LoginPage } from './LoginPage';

const loginMock = vi.fn();

vi.mock('../../services/auth.service', () => ({
  login: (...args: unknown[]) => loginMock(...args),
}));

describe('LoginPage', () => {
  beforeEach(() => loginMock.mockReset());

  it('validates required fields before calling the API', async () => {
    render(<MemoryRouter><LoginPage /></MemoryRouter>);

    fireEvent.click(screen.getByRole('button', { name: 'Đăng nhập' }));

    expect(await screen.findByRole('alert')).toHaveTextContent('Vui lòng nhập email và mật khẩu.');
    expect(loginMock).not.toHaveBeenCalled();
  });

  it('logs in and stores the returned session', async () => {
    loginMock.mockResolvedValue({
      accessToken: 'jwt-token',
      tokenType: 'Bearer',
      expiresIn: 3600,
      user: {
        id: 'user-1',
        email: 'admin@enterprise.local',
        firstName: 'Nguyễn',
        lastName: 'An',
        roles: ['ADMIN'],
      },
    });

    render(<MemoryRouter><LoginPage /></MemoryRouter>);

    fireEvent.change(screen.getByLabelText('Email'), { target: { value: 'admin@enterprise.local' } });
    fireEvent.change(screen.getByLabelText('Mật khẩu'), { target: { value: 'Admin@123' } });
    fireEvent.click(screen.getByRole('button', { name: 'Đăng nhập' }));

    await waitFor(() => expect(loginMock).toHaveBeenCalledWith({
      email: 'admin@enterprise.local',
      password: 'Admin@123',
    }));
  });
});
