import { create } from 'zustand';

export interface AuthUser {
  id: string;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  user: AuthUser | null;
  setSession: (accessToken: string, refreshToken: string, user: AuthUser) => void;
  logout: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: null,
  refreshToken: null,
  user: null,
  setSession: (accessToken, refreshToken, user) => set({ accessToken, refreshToken, user }),
  logout: () => set({ accessToken: null, refreshToken: null, user: null }),
  isAuthenticated: () => Boolean(get().accessToken && get().user),
}));
