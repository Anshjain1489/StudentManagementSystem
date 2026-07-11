import React, { createContext, useState, useEffect, useContext } from 'react';
import api from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Restore session from localStorage on app load
    const storedUser = localStorage.getItem('user');
    const token = localStorage.getItem('token');
    
    if (storedUser && token) {
      try {
        setUser(JSON.parse(storedUser));
      } catch (e) {
        localStorage.clear();
      }
    }
    setLoading(false);
  }, []);

  const login = async (usernameOrEmail, password) => {
    try {
      const res = await api.post('/auth/login', { usernameOrEmail, password });
      if (res.success && res.data) {
        const { accessToken, ...userData } = res.data;
        localStorage.setItem('token', accessToken);
        localStorage.setItem('user', JSON.stringify(userData));
        setUser(userData);
        return { success: true };
      }
      return { success: false, message: res.message || 'Login failed' };
    } catch (err) {
      return { success: false, message: err.message || 'Invalid credentials' };
    }
  };

  const logout = () => {
    try {
      api.post('/auth/logout'); // optional fire-and-forget
    } catch (e) {}
    localStorage.clear();
    setUser(null);
  };

  const hasRole = (roles) => {
    if (!user || !user.roles) return false;
    if (typeof roles === 'string') return user.roles.includes(roles);
    return roles.some((role) => user.roles.includes(role));
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, hasRole }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
