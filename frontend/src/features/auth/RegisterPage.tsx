import { useState } from 'react';
import { isAxiosError } from 'axios';
import { LockKeyhole, Mail, UserRound } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { register } from '../../services/auth.service';
import { useAuthStore } from '../../stores/auth.store';
import { Button } from '../../components/ui/Button';

export function RegisterPage() {
  const navigate = useNavigate();
  const setSession = useAuthStore((state) => state.setSession);
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');

    if (!firstName.trim() || !lastName.trim() || !email.trim() || !password) {
      setError('Vui lòng nhập đầy đủ thông tin.');
      return;
    }
    if (password.length < 8 || password.length > 72) {
      setError('Mật khẩu phải có từ 8 đến 72 ký tự.');
      return;
    }
    if (password !== confirmPassword) {
      setError('Mật khẩu xác nhận không khớp.');
      return;
    }

    try {
      setSubmitting(true);
      const response = await register({
        firstName: firstName.trim(),
        lastName: lastName.trim(),
        email: email.trim(),
        password,
      });
      setSession(response.accessToken, response.refreshToken, response.user);
      navigate('/', { replace: true });
    } catch (error) {
      if (isAxiosError(error) && error.response?.status === 409) {
        setError('Email này đã được sử dụng. Hãy đăng nhập hoặc dùng email khác.');
      } else if (isAxiosError(error) && error.response?.status === 400) {
        setError('Thông tin đăng ký không hợp lệ. Vui lòng kiểm tra lại.');
      } else {
        setError('Không thể kết nối để tạo tài khoản. Vui lòng thử lại sau.');
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-50 px-4 dark:bg-slate-950">
      <section className="w-full max-w-md rounded-3xl border border-slate-200 bg-white p-8 shadow-sm dark:border-slate-800 dark:bg-slate-900">
        <div className="mb-8">
          <p className="text-sm font-semibold text-blue-600">Enterprise Hub</p>
          <h1 className="mt-2 text-2xl font-bold text-slate-900 dark:text-white">Tạo tài khoản</h1>
          <p className="mt-1 text-sm text-slate-500">Đăng ký để bắt đầu sử dụng hệ thống.</p>
        </div>

        <form onSubmit={handleSubmit} className="space-y-5">
          <div className="grid grid-cols-2 gap-3">
            <label className="block">
              <span className="mb-2 block text-sm font-medium">Họ</span>
              <span className="relative block">
                <UserRound className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-slate-400" />
                <input value={lastName} onChange={(event) => setLastName(event.target.value)} autoComplete="family-name" className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" />
              </span>
            </label>
            <label className="block">
              <span className="mb-2 block text-sm font-medium">Tên</span>
              <input value={firstName} onChange={(event) => setFirstName(event.target.value)} autoComplete="given-name" className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" />
            </label>
          </div>

          <label className="block">
            <span className="mb-2 block text-sm font-medium">Email</span>
            <span className="relative block">
              <Mail className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-slate-400" />
              <input value={email} onChange={(event) => setEmail(event.target.value)} type="email" autoComplete="email" className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" />
            </span>
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-medium">Mật khẩu</span>
            <span className="relative block">
              <LockKeyhole className="absolute left-3 top-1/2 size-4 -translate-y-1/2 text-slate-400" />
              <input value={password} onChange={(event) => setPassword(event.target.value)} type="password" autoComplete="new-password" className="w-full rounded-xl border border-slate-200 bg-white py-2.5 pl-10 pr-3 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" />
            </span>
            <span className="mt-1 block text-xs text-slate-500">Từ 8 đến 72 ký tự.</span>
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-medium">Xác nhận mật khẩu</span>
            <input value={confirmPassword} onChange={(event) => setConfirmPassword(event.target.value)} type="password" autoComplete="new-password" className="w-full rounded-xl border border-slate-200 bg-white px-3 py-2.5 outline-none focus:border-blue-500 dark:border-slate-700 dark:bg-slate-950" />
          </label>

          {error && <p role="alert" className="text-sm text-red-600">{error}</p>}

          <Button type="submit" className="w-full justify-center" loading={submitting}>
            Đăng ký
          </Button>
        </form>

        <p className="mt-6 text-center text-sm text-slate-500">
          Đã có tài khoản?{' '}
          <Link to="/login" className="font-semibold text-blue-600 hover:text-blue-700">
            Đăng nhập
          </Link>
        </p>
      </section>
    </main>
  );
}
