import { act } from '@testing-library/react';
import { useUiStore } from './ui.store';

describe('useUiStore', () => {
  beforeEach(() => {
    act(() => {
      useUiStore.getState().reset();
    });
  });

  it('starts in light mode', () => {
    expect(useUiStore.getState().theme).toBe('light');
  });

  it('toggles between light and dark mode', () => {
    act(() => useUiStore.getState().toggleTheme());
    expect(useUiStore.getState().theme).toBe('dark');

    act(() => useUiStore.getState().toggleTheme());
    expect(useUiStore.getState().theme).toBe('light');
  });

  it('can explicitly set a theme', () => {
    act(() => useUiStore.getState().setTheme('dark'));
    expect(useUiStore.getState().theme).toBe('dark');
  });
});
