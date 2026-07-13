import React, { useState, useEffect, useRef } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Sparkles, AlertCircle, Eye, EyeOff, RefreshCw } from 'lucide-react';

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { register, handleSubmit, formState: { errors } } = useForm();
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [warmingUp, setWarmingUp] = useState(false);
  const [retryCountdown, setRetryCountdown] = useState(0);
  const countdownRef = useRef(null);
  const formDataRef = useRef(null);

  // Auto-retry countdown when server is warming up (Render free tier cold start)
  useEffect(() => {
    if (retryCountdown > 0) {
      countdownRef.current = setTimeout(() => setRetryCountdown(c => c - 1), 1000);
    } else if (retryCountdown === 0 && warmingUp) {
      setWarmingUp(false);
      if (formDataRef.current) onSubmit(formDataRef.current);
    }
    return () => clearTimeout(countdownRef.current);
  }, [retryCountdown, warmingUp]);

  const formatCountdown = (s) => `${Math.floor(s / 60)}:${String(s % 60).padStart(2, '0')}`;

  const onSubmit = async (data) => {
    formDataRef.current = data;
    setLoading(true);
    setErrorMsg('');
    setWarmingUp(false);
    const res = await login(data.usernameOrEmail, data.password);
    setLoading(false);

    if (res.success) {
      navigate('/');
    } else if (res.message && (res.message.includes('warming up') || res.message.includes('unreachable'))) {
      // Render free tier cold start — show countdown and auto-retry
      setWarmingUp(true);
      setRetryCountdown(170); // Render takes ~170s to cold-start
      setErrorMsg('');
    } else {
      setErrorMsg(res.message);
    }
  };

  return (
    <div className="d-flex align-items-center justify-content-center min-vh-100 px-3 py-5" style={{
      background: 'radial-gradient(circle at center, #111827 0%, #030712 100%)'
    }}>
      <div className="glass-panel p-4 p-sm-5 w-100 animated-fade-in" style={{ maxWidth: '480px' }}>
        <div className="text-center mb-4">
          <div className="d-flex align-items-center justify-content-center gap-2 mb-2">
            <Sparkles className="text-info animate-pulse" size={32} />
            <h1 className="fw-bold fs-2 gradient-text mb-0">SMS Portal</h1>
          </div>
          <p className="text-secondary small">Enter your credential details to access the academic system</p>
        </div>

        {searchParams.get('expired') && (
          <div className="alert alert-warning d-flex align-items-center gap-2 small" role="alert">
            <AlertCircle size={16} />
            <span>Session expired. Please log in again.</span>
          </div>
        )}

        {/* Server warming up banner */}
        {warmingUp && (
          <div className="alert d-flex align-items-center gap-2 small mb-3" role="alert"
            style={{ background: 'rgba(59,130,246,0.15)', border: '1px solid rgba(59,130,246,0.4)', color: '#93c5fd', borderRadius: '10px' }}>
            <RefreshCw size={16} className="flex-shrink-0" style={{ animation: 'spin 1s linear infinite' }} />
            <div>
              <strong>Server is starting up</strong> (Render free tier cold start)<br />
              <span className="opacity-75">This takes ~3 minutes on first access. Auto-retrying in </span>
              <strong>{formatCountdown(retryCountdown)}</strong><span className="opacity-75">…</span>
            </div>
          </div>
        )}

        {errorMsg && (
          <div className="alert alert-danger d-flex align-items-center gap-2 small" role="alert">
            <AlertCircle size={16} />
            <span>{errorMsg}</span>
          </div>
        )}

        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="mb-3">
            <label className="form-label text-secondary small fw-medium">Username or Email</label>
            <input
              type="text"
              placeholder="e.g. admin@sms.edu"
              className={`form-control form-glass ${errors.usernameOrEmail ? 'is-invalid' : ''}`}
              {...register('usernameOrEmail', { required: 'Username or email is required' })}
            />
            {errors.usernameOrEmail && (
              <div className="invalid-feedback text-danger small mt-1">{errors.usernameOrEmail.message}</div>
            )}
          </div>

          <div className="mb-4">
            <label className="form-label text-secondary small fw-medium">Password</label>
            <div className="position-relative">
              <input
                type={showPassword ? 'text' : 'password'}
                placeholder="••••••••"
                className={`form-control form-glass pe-5 ${errors.password ? 'is-invalid' : ''}`}
                {...register('password', { required: 'Password is required' })}
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="position-absolute end-0 top-50 translate-middle-y btn btn-link text-secondary pe-3 py-0"
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            {errors.password && (
              <div className="invalid-feedback text-danger small mt-1">{errors.password.message}</div>
            )}
          </div>

          <button
            type="submit"
            className="btn-premium-primary w-100 py-3 d-flex align-items-center justify-content-center gap-2"
            disabled={loading || warmingUp}
          >
            {loading
              ? <><RefreshCw size={16} style={{ animation: 'spin 1s linear infinite' }} /> Logging in… (may take 2-3 min on cold start)</>              : warmingUp
              ? `Auto-retrying in ${formatCountdown(retryCountdown)}…`
              : 'Access Portal'}
          </button>
        </form>

        <div className="text-center mt-4">
          <small className="text-secondary">Default Admin: admin@sms.edu / Admin@123</small>
        </div>
      </div>
    </div>
  );
};

export default Login;
