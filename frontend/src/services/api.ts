import axios, { type AxiosError, type AxiosRequestConfig } from 'axios';
import { useAuthStore } from '../stores/auth.store';

type RetryableRequestConfig = AxiosRequestConfig & {
  _retry?: boolean;
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1';

export const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

let refreshPromise: Promise<string | null> | null = null;

const isAuthEndpoint = (url?: string) =>
  Boolean(url && /^\/auth\/(login|refresh|logout)(?:$|\/)/.test(url));

const refreshAccessToken = async (): Promise<string | null> => {
  const { refreshToken, user } = useAuthStore.getState();
  if (!refreshToken || !user) {
    return null;
  }

  if (!refreshPromise) {
    refreshPromise = axios
      .post(
        `${API_BASE_URL}/auth/refresh`,
        { refreshToken },
        { headers: { 'Content-Type': 'application/json' } },
      )
      .then(({ data }) => {
        useAuthStore.getState().setSession(
          data.accessToken,
          data.refreshToken,
          data.user,
        );
        return data.accessToken as string;
      })
      .catch(() => {
        useAuthStore.getState().logout();
        return null;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
};

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetryableRequestConfig | undefined;

    if (
      error.response?.status !== 401 ||
      !originalRequest ||
      originalRequest._retry ||
      isAuthEndpoint(originalRequest.url)
    ) {
      if (error.response?.status === 401 && isAuthEndpoint(originalRequest?.url)) {
        useAuthStore.getState().logout();
      }
      return Promise.reject(error);
    }

    const newAccessToken = await refreshAccessToken();
    if (!newAccessToken) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;
    originalRequest.headers = originalRequest.headers ?? {};
    originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;

    return api(originalRequest);
  },
);
