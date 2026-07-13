import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'https://studentmanagementsystem-eqgk.onrender.com/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 180000, // 3 minute timeout — Render free tier cold starts take ~2-3 minutes
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor to handle errors globally (e.g. 401 unauth)
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response) {
      // Session expired or invalid credentials
      if (error.response.status === 401 && !window.location.pathname.includes('/login')) {
        localStorage.clear();
        window.location.href = '/login?expired=true';
      }
      return Promise.reject(error.response.data || { message: 'Something went wrong' });
    }
    if (error.code === 'ECONNABORTED' || error.message?.includes('timeout')) {
      return Promise.reject({ message: 'Server is warming up. Please wait 30 seconds and try again.' });
    }
    return Promise.reject({ message: 'Server is unreachable. Please try again.' });
  }
);

export default api;
