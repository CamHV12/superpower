import { create } from 'zustand';

export type Theme = 'light' | 'dark';

interface UiState {
  theme: Theme;
  setTheme: (theme: Theme) => void;
  toggleTheme: () => void;
  reset: () => void;
}

const initialState: Pick<UiState, 'theme'> = {
  theme: 'light',
};

export const useUiStore = create<UiState>((set) => ({
  ...initialState,
  setTheme: (theme) => set({ theme }),
  toggleTheme: () =>
    set((state) => ({
      theme: state.theme === 'light' ? 'dark' : 'light',
    })),
  reset: () => set(initialState),
}));
