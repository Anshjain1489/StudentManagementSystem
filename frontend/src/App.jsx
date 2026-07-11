import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import Sidebar from './components/Sidebar';
import AiAssistant from './components/AiAssistant';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Students from './pages/Students';
import Teachers from './pages/Teachers';
import Courses from './pages/Courses';
import Attendance from './pages/Attendance';
import Fees from './pages/Fees';
import Results from './pages/Results';

// Guard for authenticated users
const ProtectedRoute = ({ children }) => {
  const { user, loading } = useAuth();
  if (loading) return <div className="text-center py-5 text-secondary">Verifying credentials...</div>;
  if (!user) return <Navigate to="/login" replace />;
  return (
    <div className="d-flex">
      <Sidebar />
      <main className="main-content flex-grow-1">
        {children}
      </main>
      <AiAssistant />
    </div>
  );
};

// Guard for role authorization
const RoleRoute = ({ children, allowedRoles }) => {
  const { user, hasRole, loading } = useAuth();
  if (loading) return <div className="text-center py-5 text-secondary">Verifying authorization...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (!hasRole(allowedRoles)) return <Navigate to="/" replace />;
  return children;
};

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/login" element={<Login />} />
          
          <Route path="/" element={
            <ProtectedRoute>
              <Dashboard />
            </ProtectedRoute>
          } />
          
          <Route path="/students" element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={['ROLE_ADMIN']}>
                <Students />
              </RoleRoute>
            </ProtectedRoute>
          } />

          <Route path="/teachers" element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={['ROLE_ADMIN']}>
                <Teachers />
              </RoleRoute>
            </ProtectedRoute>
          } />

          <Route path="/courses" element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={['ROLE_ADMIN']}>
                <Courses />
              </RoleRoute>
            </ProtectedRoute>
          } />

          <Route path="/attendance" element={
            <ProtectedRoute>
              <RoleRoute allowedRoles={['ROLE_ADMIN', 'ROLE_TEACHER']}>
                <Attendance />
              </RoleRoute>
            </ProtectedRoute>
          } />

          <Route path="/fees" element={
            <ProtectedRoute>
              <Fees />
            </ProtectedRoute>
          } />

          <Route path="/results" element={
            <ProtectedRoute>
              <Results />
            </ProtectedRoute>
          } />

          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Router>
    </AuthProvider>
  );
};

export default App;
