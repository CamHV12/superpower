import { StrictMode, useEffect } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { AppRoutes } from './routes/AppRoutes';
import { useUiStore } from './stores/ui.store';
import './index.css';

function ThemeController() {
  const theme = useUiStore((state) => state.theme);

  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark');
  }, [theme]);

  return null;
}

function App() {
  return (
    <BrowserRouter>
      <ThemeController />
      <AppRoutes />
    </BrowserRouter>
  );
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>,
);
