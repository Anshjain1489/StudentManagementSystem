import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { CalendarRange, Sparkles, CheckCircle2, UserCheck, QrCode, X } from 'lucide-react';

const Attendance = () => {
  const [courses, setCourses] = useState([]);
  const [selectedCourse, setSelectedCourse] = useState('');
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [attendanceRecords, setAttendanceRecords] = useState({});
  const [qrCodeData, setQrCodeData] = useState('');

  const fetchCourses = async () => {
    try {
      const res = await api.get('/courses');
      if (res.success) {
        setCourses(res.data.content || []);
        if (res.data.content?.length > 0) {
          setSelectedCourse(res.data.content[0].id);
        }
      }
    } catch (err) {
      console.error(err);
    }
  };

  const fetchStudents = async () => {
    if (!selectedCourse) return;
    try {
      setLoading(true);
      // Fetch students enrolled in course
      const res = await api.get('/students', { params: { size: 100 } }); // mock filter
      if (res.success) {
        setStudents(res.data.content || []);
        
        // Load existing attendance if any
        const attRes = await api.get(`/attendance/course/${selectedCourse}/date/${selectedDate}`);
        const defaultAtt = {};
        students.forEach((s) => { defaultAtt[s.id] = 'PRESENT'; });
        if (attRes.success && attRes.data) {
          attRes.data.forEach((rec) => {
            defaultAtt[rec.studentId] = rec.status;
          });
        }
        setAttendanceRecords(defaultAtt);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCourses();
  }, []);

  useEffect(() => {
    fetchStudents();
  }, [selectedCourse, selectedDate]);

  const handleStatusChange = (studentId, status) => {
    setAttendanceRecords((prev) => ({
      ...prev,
      [studentId]: status
    }));
  };

  const submitAttendance = async () => {
    try {
      const payload = Object.keys(attendanceRecords).map((studentId) => ({
        studentId: parseInt(studentId),
        courseId: parseInt(selectedCourse),
        date: selectedDate,
        status: attendanceRecords[studentId],
        remarks: 'Class session'
      }));

      await api.post('/attendance/bulk', payload);
      alert('Attendance saved successfully!');
    } catch (err) {
      alert(err.message || 'Failed to submit attendance');
    }
  };

  const generateQrCheckIn = async () => {
    try {
      const res = await api.get(`/attendance/qr/course/${selectedCourse}`, {
        params: { date: selectedDate }
      });
      if (res.success) {
        setQrCodeData(res.data);
      }
    } catch (err) {
      alert('Failed to generate attendance QR code');
    }
  };

  return (
    <div className="animated-fade-in">
      <div className="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-3">
        <div>
          <h2 className="fw-bold gradient-text mb-1">Attendance Roll Call</h2>
          <p className="text-secondary mb-0">Record daily class attendance, check status limits, or initialize secure QR check-in.</p>
        </div>
        <button className="btn btn-outline-info d-flex align-items-center gap-2" onClick={generateQrCheckIn}>
          <QrCode size={18} />
          <span>Interactive QR Code</span>
        </button>
      </div>

      {/* Filter Selection Panel */}
      <div className="glass-panel p-4 mb-4">
        <div className="row g-3">
          <div className="col-12 col-md-6">
            <label className="form-label text-secondary small">Course Subject</label>
            <select 
              value={selectedCourse}
              onChange={(e) => setSelectedCourse(e.target.value)}
              className="form-select form-glass"
            >
              {courses.map((course) => (
                <option key={course.id} value={course.id}>{course.code} - {course.name}</option>
              ))}
            </select>
          </div>
          <div className="col-12 col-md-6">
            <label className="form-label text-secondary small">Date</label>
            <input 
              type="date"
              value={selectedDate}
              onChange={(e) => setSelectedDate(e.target.value)}
              className="form-control form-glass"
            />
          </div>
        </div>
      </div>

      {/* Roster & Actions */}
      <div className="glass-panel p-3 mb-4">
        {loading ? (
          <div className="text-center py-5 text-secondary">Loading roster...</div>
        ) : students.length === 0 ? (
          <div className="text-center py-5 text-secondary">No students enrolled in this course division.</div>
        ) : (
          <>
            <table className="table-glass mb-4">
              <thead>
                <tr>
                  <th>Student</th>
                  <th>Roll Number</th>
                  <th className="text-end">Roster Attendance Roll</th>
                </tr>
              </thead>
              <tbody>
                {students.map((student) => (
                  <tr key={student.id}>
                    <td><span className="fw-semibold">{student.fullName}</span></td>
                    <td><code>{student.rollNumber}</code></td>
                    <td className="text-end">
                      <div className="d-flex gap-3 justify-content-end align-items-center">
                        <label className="d-flex align-items-center gap-2 cursor-pointer small">
                          <input 
                            type="radio" 
                            name={`att-${student.id}`} 
                            checked={attendanceRecords[student.id] === 'PRESENT'}
                            onChange={() => handleStatusChange(student.id, 'PRESENT')}
                            className="form-check-input"
                          />
                          <span className="text-success fw-medium">Present</span>
                        </label>
                        <label className="d-flex align-items-center gap-2 cursor-pointer small">
                          <input 
                            type="radio" 
                            name={`att-${student.id}`} 
                            checked={attendanceRecords[student.id] === 'ABSENT'}
                            onChange={() => handleStatusChange(student.id, 'ABSENT')}
                            className="form-check-input"
                          />
                          <span className="text-danger fw-medium">Absent</span>
                        </label>
                        <label className="d-flex align-items-center gap-2 cursor-pointer small">
                          <input 
                            type="radio" 
                            name={`att-${student.id}`} 
                            checked={attendanceRecords[student.id] === 'LATE'}
                            onChange={() => handleStatusChange(student.id, 'LATE')}
                            className="form-check-input"
                          />
                          <span className="text-warning fw-medium">Late</span>
                        </label>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            
            <div className="d-flex justify-content-end">
              <button onClick={submitAttendance} className="btn-premium-primary d-flex align-items-center gap-2">
                <UserCheck size={18} />
                <span>Save Roster Attendance</span>
              </button>
            </div>
          </>
        )}
      </div>

      {/* QR Code Modal Overlay */}
      {qrCodeData && (
        <div className="modal-backdrop show d-flex align-items-center justify-content-center p-3" style={{ background: 'rgba(0,0,0,0.8)' }}>
          <div className="glass-panel p-4 text-center animated-fade-in" style={{ maxWidth: '380px' }}>
            <div className="d-flex justify-content-end mb-2">
              <button className="btn btn-link text-secondary p-0" onClick={() => setQrCodeData('')}>
                <X size={20} />
              </button>
            </div>
            <h5 className="fw-bold mb-3">Self Check-In QR Code</h5>
            <p className="text-secondary small mb-4">Students can scan this code using their mobile cameras to automatically log attendance.</p>
            <div className="p-3 bg-white rounded-3 mb-4 d-inline-block">
              <img src={qrCodeData} alt="Attendance QR Code" style={{ width: '220px', height: '220px' }} />
            </div>
            <div>
              <button className="btn btn-premium-secondary w-100" onClick={() => setQrCodeData('')}>Close Window</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Attendance;
