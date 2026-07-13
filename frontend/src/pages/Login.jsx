import React, { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Sparkles, AlertCircle, Eye, EyeOff } from 'lucide-react';

const Login = () => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const { register, handleSubmit, formState: { errors } } = useForm();
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const onSubmit = async (data) => {
    setLoading(true);
    setErrorMsg('');
    const res = await login(data.usernameOrEmail, data.password);
    setLoading(false);
    
    if (res.success) {
      navigate('/');
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
            disabled={loading}
          >
            {loading ? 'Logging in...' : 'Access Portal'}
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
