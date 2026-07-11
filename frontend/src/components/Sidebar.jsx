import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { 
  LayoutDashboard, 
  Users, 
  UserSquare2, 
  BookOpen, 
  CalendarRange, 
  CreditCard, 
  GraduationCap, 
  Bell, 
  LogOut,
  Sparkles
} from 'lucide-react';

const Sidebar = () => {
  const { user, logout, hasRole } = useAuth();

  if (!user) return null;

  return (
    <aside className="sidebar">
      <div>
        <div className="d-flex align-items-center gap-2 px-3 py-2 mb-4">
          <Sparkles className="text-info" size={28} />
          <span className="logo-text fw-bold fs-4 gradient-text">SMS Portal</span>
        </div>

        <nav>
          <NavLink to="/" className={({ isActive }) => `nav-btn ${isActive ? 'active' : ''}`}>
            <LayoutDashboard size={20} />
            <span className="nav-label">Dashboard</span>
          </NavLink>

          {hasRole('ROLE_ADMIN') && (
            <>
              <NavLink to="/students" className={({ isActive }) => `nav-btn ${isActive ? 'active' : ''}`}>
                <Users size={20} />
                <span className="nav-label">Students</span>
              </NavLink>
              <NavLink to="/teachers" className={({ isActive }) => `nav-btn ${isActive ? 'active' : ''}`}>
                <UserSquare2 size={20} />
                <span className="nav-label">Teachers</span>
              </NavLink>
              <NavLink to="/courses" className={({ isActive }) => `nav-btn ${isActive ? 'active' : ''}`}>
                <BookOpen size={20} />
                <span className="nav-label">Courses</span>
              </NavLink>
            </>
          )}

          {hasRole(['ROLE_ADMIN', 'ROLE_TEACHER']) && (
            <NavLink to="/attendance" className={({ isActive }) => `nav-btn ${isActive ? 'active' : ''}`}>
              <CalendarRange size={20} />
              <span className="nav-label">Attendance</span>
            </NavLink>
          )}

          <NavLink to="/results" className={({ isActive }) => `nav-btn ${isActive ? 'active' : ''}`}>
            <GraduationCap size={20} />
            <span className="nav-label">Grades & Exams</span>
          </NavLink>

          <NavLink to="/fees" className={({ isActive }) => `nav-btn ${isActive ? 'active' : ''}`}>
            <CreditCard size={20} />
            <span className="nav-label">Fees & Payments</span>
          </NavLink>
        </nav>
      </div>

      <div className="border-top border-secondary pt-3">
        <div className="px-3 mb-3 d-none d-lg-block">
          <p className="small text-secondary mb-0">Logged in as</p>
          <p className="fw-semibold text-truncate mb-0" style={{ maxWidth: '200px' }}>{user.email}</p>
          <span className="badge badge-custom bg-secondary text-info text-capitalize mt-1" style={{ fontSize: '0.75rem' }}>
            {user.roles?.[0]?.replace('ROLE_', '').toLowerCase()}
          </span>
        </div>
        <button className="nav-btn text-danger w-100" onClick={logout}>
          <LogOut size={20} />
          <span className="nav-label">Logout</span>
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
