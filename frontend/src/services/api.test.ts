import axios from 'axios';
import { beforeEach, describe, expect, it, vi } from 'vitest';

const mocks = vi.hoisted(() => {
  const requestUse = vi.fn();
  const responseUse = vi.fn();
  const instance = Object.assign(vi.fn(), {
    interceptors: {
      request: { use: requestUse },
      response: { use: responseUse },
    },
  });
  return { requestUse, responseUse, instance, axiosPost: vi.fn() };
});

vi.mock('axios', () => ({
  default: {
    create: vi.fn(() => mocks.instance),
    post: mocks.axiosPost,
  },
}));

import { useAuthStore } from '../stores/auth.store';

describe('api authentication interceptor', () => {
  let onRejected: (error: any) => Promise<unknown>;
  let onRequest: (config: any) => any;
  let api: any;

  beforeEach(async () => {
    vi.resetModules();
    mocks.axiosPost.mockReset();
    mocks.requestUse.mockClear();
    mocks.responseUse.mockClear();
    useAuthStore.getState().logout();

    const module = await import('./api');
    api = module.api;
    const responseRegistration = mocks.responseUse.mock.calls[0];
    onRejected = responseRegistration[1];
    onRequest = mocks.requestUse.mock.calls[0][0];
  });

  it('adds the current access token to outgoing requests', () => {
    useAuthStore.getState().setSession('access-1', 'refresh-1', {
      id: 'user-1',
      email: 'admin@enterprise.local',
      firstName: 'Admin',
      lastName: 'User',
      roles: ['ADMIN'],
    });

    const config = { headers: {} };
    expect(onRequest(config)).toBe(config);
    expect(config.headers.Authorization).toBe('Bearer access-1');
  });

  it('refreshes once and retries a request after a 401', async () => {
    const user = {
      id: 'user-1',
      email: 'admin@enterprise.local',
      firstName: 'Admin',
      lastName: 'User',
      roles: ['ADMIN'],
    };
    useAuthStore.getState().setSession('access-old', 'refresh-1', user);

    mocks.axiosPost.mockResolvedValueOnce({
      data: {
        accessToken: 'access-new',
        refreshToken: 'refresh-2',
        user,
      },
    });
    mocks.instance.mockResolvedValueOnce({ data: { ok: true } });

    const originalRequest = { url: '/projects', headers: {} };
    const error = {
      response: { status: 401 },
      config: originalRequest,
    };

    const result = await onRejected(error);

    expect(mocks.axiosPost).toHaveBeenCalledWith(
      'http://localhost:8080/api/v1/auth/refresh',
      { refreshToken: 'refresh-1' },
      { headers: { 'Content-Type': 'application/json' } },
    );
    expect(useAuthStore.getState().accessToken).toBe('access-new');
    expect(useAuthStore.getState().refreshToken).toBe('refresh-2');
    expect(originalRequest._retry).toBe(true);
    expect(originalRequest.headers.Authorization).toBe('Bearer access-new');
    expect(mocks.instance).toHaveBeenCalledWith(originalRequest);
    expect(result).toEqual({ data: { ok: true } });
  });

  it('clears the session when refresh fails', async () => {
    useAuthStore.getState().setSession('access-old', 'refresh-1', {
      id: 'user-1',
      email: 'admin@enterprise.local',
      firstName: 'Admin',
      lastName: 'User',
      roles: ['ADMIN'],
    });
    mocks.axiosPost.mockRejectedValueOnce(new Error('refresh failed'));

    const error = {
      response: { status: 401 },
      config: { url: '/projects', headers: {} },
    };

    await expect(onRejected(error)).rejects.toBe(error);
    expect(useAuthStore.getState().accessToken).toBeNull();
    expect(useAuthStore.getState().refreshToken).toBeNull();
    expect(useAuthStore.getState().user).toBeNull();
  });

  it('does not refresh authentication endpoints', async () => {
    useAuthStore.getState().setSession('access-old', 'refresh-1', {
      id: 'user-1',
      email: 'admin@enterprise.local',
      firstName: 'Admin',
      lastName: 'User',
      roles: ['ADMIN'],
    });

    const error = {
      response: { status: 401 },
      config: { url: '/auth/login', headers: {} },
    };

    await expect(onRejected(error)).rejects.toBe(error);
    expect(mocks.axiosPost).not.toHaveBeenCalled();
    expect(useAuthStore.getState().accessToken).toBeNull();
  });
});
