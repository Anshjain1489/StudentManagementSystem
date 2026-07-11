import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/AuthContext';
import { GraduationCap, Trophy, Plus, X } from 'lucide-react';

const Results = () => {
  const { user, hasRole } = useAuth();
  const [results, setResults] = useState([]);
  const [exams, setExams] = useState([]);
  const [students, setStudents] = useState([]);
  const [cgpa, setCgpa] = useState(0.0);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const { register, handleSubmit, reset } = useForm();

  const fetchResults = async () => {
    try {
      setLoading(true);
      let res;
      if (hasRole('ROLE_STUDENT')) {
        res = await api.get(`/exams/students/${user.id}/results`); // mock ID identification
        const cgpaRes = await api.get(`/exams/students/${user.id}/cgpa`);
        if (cgpaRes.success) setCgpa(cgpaRes.data || 0.0);
      } else {
        res = await api.get('/exams'); // mock generic fetching
      }
      if (res.success) {
        setResults(res.data.content || res.data || []);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchExamsAndStudents = async () => {
    try {
      const examRes = await api.get('/exams');
      if (examRes.success) setExams(examRes.data.content || []);
      const studRes = await api.get('/students');
      if (studRes.success) setStudents(studRes.data.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchResults();
    if (hasRole(['ROLE_ADMIN', 'ROLE_TEACHER'])) {
      fetchExamsAndStudents();
    }
  }, []);

  const openAddModal = () => {
    reset({
      studentId: students[0]?.id || '',
      examId: exams[0]?.id || '',
      marksObtained: 85,
      remarks: 'Excellent performance'
    });
    setIsModalOpen(true);
  };

  const onSubmit = async (data) => {
    try {
      await api.post('/exams/results', data);
      setIsModalOpen(false);
      fetchResults();
    } catch (err) {
      alert(err.message || 'Operation failed');
    }
  };

  return (
    <div className="animated-fade-in">
      <div className="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-3">
        <div>
          <h2 className="fw-bold gradient-text mb-1">Academic Grades & Exams</h2>
          <p className="text-secondary mb-0">Review exam schedules, academic course grading lists, and student report card records.</p>
        </div>
        {hasRole(['ROLE_ADMIN', 'ROLE_TEACHER']) ? (
          <button className="btn-premium-primary d-flex align-items-center gap-2" onClick={openAddModal}>
            <Plus size={18} />
            <span>Enter Grades</span>
          </button>
        ) : (
          <div className="glass-panel px-4 py-2 d-flex align-items-center gap-2">
            <Trophy size={18} className="text-warning" />
            <span>CGPA: <strong className="text-info">{cgpa?.toFixed(2) || '0.00'} / 4.00</strong></span>
          </div>
        )}
      </div>

      {loading ? (
        <div className="text-center py-5 text-secondary">Loading grade books...</div>
      ) : results.length === 0 ? (
        <div className="text-center py-5 text-secondary">No exam results entered.</div>
      ) : (
        <div className="glass-panel p-3 overflow-x-auto">
          <table className="table-glass">
            <thead>
              <tr>
                <th>Student</th>
                <th>Exam Name</th>
                <th>Course</th>
                <th>Marks Obtained</th>
                <th>Passing Marks</th>
                <th>Percentage</th>
                <th>Grade</th>
                <th>GPA</th>
                <th>Result</th>
              </tr>
            </thead>
            <tbody>
              {results.map((res) => (
                <tr key={res.id}>
                  <td>
                    <span className="fw-semibold d-block">{res.studentName || 'Student'}</span>
                    <small className="text-secondary">{res.rollNumber || 'CS-Roster'}</small>
                  </td>
                  <td>{res.examName}</td>
                  <td>{res.courseName}</td>
                  <td><strong className="text-light">{res.marksObtained}</strong> / {res.totalMarks}</td>
                  <td>{res.passingMarks || 40}</td>
                  <td>{res.percentage?.toFixed(1)}%</td>
                  <td><span className="badge badge-custom bg-secondary text-info">{res.grade}</span></td>
                  <td>{res.gpa?.toFixed(2)}</td>
                  <td>
                    <span className={`badge badge-custom ${res.isPass ? 'badge-paid' : 'badge-pending'}`}>
                      {res.isPass ? 'Pass' : 'Fail'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Grade Entry Modal */}
      {isModalOpen && (
        <div className="modal-backdrop show d-flex align-items-center justify-content-center p-3" style={{ background: 'rgba(0,0,0,0.8)' }}>
          <div className="glass-panel p-4 w-100 animated-fade-in" style={{ maxWidth: '500px' }}>
            <div className="d-flex align-items-center justify-content-between mb-4 border-bottom border-secondary pb-3">
              <h5 className="fw-bold mb-0">Record Student Exam Score</h5>
              <button className="btn btn-link text-secondary p-0" onClick={() => setIsModalOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)}>
              <div className="mb-3">
                <label className="form-label text-secondary small">Select Student</label>
                <select className="form-select form-glass" {...register('studentId', { required: true })}>
                  {students.map((s) => (
                    <option key={s.id} value={s.id}>{s.fullName} ({s.rollNumber})</option>
                  ))}
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label text-secondary small">Select Exam Session</label>
                <select className="form-select form-glass" {...register('examId', { required: true })}>
                  {exams.map((e) => (
                    <option key={e.id} value={e.id}>{e.name} ({e.courseName})</option>
                  ))}
                </select>
              </div>

              <div className="mb-3">
                <label className="form-label text-secondary small">Marks Obtained</label>
                <input type="number" step="0.5" className="form-control form-glass" {...register('marksObtained', { required: true, valueAsNumber: true })} />
              </div>

              <div className="mb-4">
                <label className="form-label text-secondary small">Remarks</label>
                <input type="text" className="form-control form-glass" {...register('remarks')} />
              </div>

              <div className="d-flex justify-content-end gap-2">
                <button type="button" className="btn btn-premium-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn-premium-primary">Record Grade</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Results;
