import { act } from '@testing-library/react';
import { useAuthStore } from './auth.store';

describe('useAuthStore', () => {
  beforeEach(() => {
    act(() => useAuthStore.getState().logout());
  });

  it('stores authenticated user and token', () => {
    const user = {
      id: 'user-1',
      email: 'admin@enterprise.local',
      firstName: 'Nguyễn',
      lastName: 'An',
      roles: ['ADMIN'],
    };

    act(() => useAuthStore.getState().setSession('jwt-token', 'refresh-token', user));

    expect(useAuthStore.getState().accessToken).toBe('jwt-token');
    expect(useAuthStore.getState().refreshToken).toBe('refresh-token');
    expect(useAuthStore.getState().user).toEqual(user);
    expect(useAuthStore.getState().isAuthenticated()).toBe(true);
  });

  it('clears the session on logout', () => {
    act(() => useAuthStore.getState().setSession('jwt-token', 'refresh-token', {
      id: 'user-1',
      email: 'admin@enterprise.local',
      firstName: 'Nguyễn',
      lastName: 'An',
      roles: ['ADMIN'],
    }));
    act(() => useAuthStore.getState().logout());

    expect(useAuthStore.getState().accessToken).toBeNull();
    expect(useAuthStore.getState().refreshToken).toBeNull();
    expect(useAuthStore.getState().user).toBeNull();
    expect(useAuthStore.getState().isAuthenticated()).toBe(false);
  });
});
