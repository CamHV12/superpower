import { useState } from 'react';
import { LockKeyhole, Mail } from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { login } from '../../services/auth.service';
import { useAuthStore } from '../../stores/auth.store';
import { Button } from '../../components/ui/Button';

export function LoginPage() {
  const navigate = useNavigate();
  const setSession = useAuthStore((state) => state.setSession);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');

    if (!email.trim() || !password) {
      setError('Vui lòng nhập email và mật khẩu.');
      return;
    }

    try {
      setSubmitting(true);
      const response = await login({ email: email.trim(), password });
      setSession(response.accessToken, response.user);
      navigate('/', { replace: true });
    } catch {
      setError('Email hoặc mật khẩu không chính xác.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-slate-950">
      <section className="w-full max-w-md rounded-3xl border border-slate-200 bg-white p-8 shadow-sm dark:border-slate-800 dark:bg-slate-900">
        <div className="mb-8">
          <p className="text-sm font-semibold text-blue-600">Enterprise Hub</p>
          <h1 className="mt-2 text-2xl font-bold text-slate-900 dark:text-white">Đăng nhập</h1>
          <p className="mt-1 text-sm text-slate-500">Đăng nhập để quản lý doanh nghiệp.</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <label className="block">
            <span className="mb-2 block text-sm font-medium">Email</span>
            <span className="relative block">
              <Mail className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-slate-400" />
              <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" autoComplete="email" className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" placeholder="admin@enterprise.local" />
            </span>
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-medium">Mật khẩu</span>
            <span className="relative block">
              <LockKeyhole className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-slate-400" />
              <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" autoComplete="current-password" className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" placeholder="••••••••" />
            </span>
          </label>

          {error && <p role="alert" className="text-sm text-red-600">{error}</p>}

          <Button type="submit" className="w-full justify-center" loading={submitting}>
            Đăng nhập
          </Button>
        </form>
      </section>
    </main>
  );
}
