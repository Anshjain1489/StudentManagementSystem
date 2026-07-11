import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar } from 'recharts';
import { Users, BookOpen, GraduationCap, DollarSign, Wallet } from 'lucide-react';

const chartData = [
  { name: 'CS', Students: 120, Courses: 14, Budget: 4200 },
  { name: 'EE', Students: 98, Courses: 11, Budget: 3100 },
  { name: 'ME', Students: 85, Courses: 9, Budget: 2800 },
  { name: 'CE', Students: 72, Courses: 8, Budget: 2200 },
  { name: 'IT', Students: 110, Courses: 12, Budget: 3900 },
];

const Dashboard = () => {
  const { user, hasRole } = useAuth();
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchStats = async () => {
      try {
        let endpoint = '/dashboard/admin';
        if (hasRole('ROLE_TEACHER')) endpoint = '/dashboard/teacher';
        if (hasRole('ROLE_STUDENT')) endpoint = '/dashboard/student';

        const res = await api.get(endpoint);
        if (res.success) {
          setStats(res.data);
        }
      } catch (err) {
        console.error('Failed to load dashboard metrics', err);
      } finally {
        setLoading(false);
      }
    };

    fetchStats();
  }, [hasRole]);

  return (
    <div className="animated-fade-in">
      <div className="mb-4">
        <h2 className="fw-bold gradient-text mb-1">Academic Dashboard</h2>
        <p className="text-secondary">Welcome back, {user?.username}. Here's the institutional overview.</p>
      </div>

      {/* Metrics Cards */}
      <div className="row g-4 mb-5">
        <div className="col-12 col-sm-6 col-lg-3">
          <div className="glass-panel p-4 d-flex align-items-center justify-content-between">
            <div>
              <p className="small text-secondary mb-1">Total Students</p>
              <h3 className="fw-bold mb-0">{loading ? '...' : stats?.totalStudents ?? 1}</h3>
            </div>
            <div className="p-3 bg-primary bg-opacity-10 text-primary rounded-3">
              <Users size={24} />
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-lg-3">
          <div className="glass-panel p-4 d-flex align-items-center justify-content-between">
            <div>
              <p className="small text-secondary mb-1">Total Teachers</p>
              <h3 className="fw-bold mb-0">{loading ? '...' : stats?.totalTeachers ?? 1}</h3>
            </div>
            <div className="p-3 bg-info bg-opacity-10 text-info rounded-3">
              <GraduationCap size={24} />
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-lg-3">
          <div className="glass-panel p-4 d-flex align-items-center justify-content-between">
            <div>
              <p className="small text-secondary mb-1">Active Courses</p>
              <h3 className="fw-bold mb-0">{loading ? '...' : stats?.totalCourses ?? 1}</h3>
            </div>
            <div className="p-3 bg-purple bg-opacity-10 text-purple rounded-3" style={{ color: '#a855f7' }}>
              <BookOpen size={24} />
            </div>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-lg-3">
          <div className="glass-panel p-4 d-flex align-items-center justify-content-between">
            <div>
              <p className="small text-secondary mb-1">Total Collected</p>
              <h3 className="fw-bold mb-0">${loading ? '...' : (stats?.totalFeesCollected || '0.00')}</h3>
            </div>
            <div className="p-3 bg-success bg-opacity-10 text-success rounded-3">
              <DollarSign size={24} />
            </div>
          </div>
        </div>
      </div>

      {/* Analytics Charts */}
      <div className="row g-4 mb-4">
        <div className="col-12 col-xl-7">
          <div className="glass-panel p-4 h-100">
            <h5 className="fw-bold mb-4">Enrollment & Growth Trend</h5>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <AreaChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="colorStudents" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.4}/>
                      <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
                  <XAxis dataKey="name" stroke="#94a3b8" />
                  <YAxis stroke="#94a3b8" />
                  <Tooltip contentStyle={{ background: '#121829', border: '1px solid rgba(255,255,255,0.08)' }} />
                  <Area type="monotone" dataKey="Students" stroke="#3b82f6" fillOpacity={1} fill="url(#colorStudents)" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>

        <div className="col-12 col-xl-5">
          <div className="glass-panel p-4 h-100">
            <h5 className="fw-bold mb-4">Department Course Load</h5>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer>
                <BarChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" />
                  <XAxis dataKey="name" stroke="#94a3b8" />
                  <YAxis stroke="#94a3b8" />
                  <Tooltip contentStyle={{ background: '#121829', border: '1px solid rgba(255,255,255,0.08)' }} />
                  <Bar dataKey="Courses" fill="#06b6d4" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
