import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { useForm } from 'react-hook-form';
import { Search, UserPlus, Edit2, Trash2, X, Upload } from 'lucide-react';

const Students = () => {
  const [students, setStudents] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingStudent, setEditingStudent] = useState(null);
  const [uploadFile, setUploadFile] = useState(null);

  const { register, handleSubmit, reset, setValue } = useForm();

  const fetchStudents = async () => {
    try {
      setLoading(true);
      const res = await api.get('/students', {
        params: { page, size: 8, search }
      });
      if (res.success && res.data) {
        setStudents(res.data.content || []);
        setTotalPages(res.data.totalPages || 0);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchDepartments = async () => {
    try {
      const res = await api.get('/departments');
      if (res.success) {
        setDepartments(res.data || []);
      }
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchStudents();
  }, [page, search]);

  useEffect(() => {
    fetchDepartments();
  }, []);

  const openAddModal = () => {
    setEditingStudent(null);
    reset({
      rollNumber: '',
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      gender: 'MALE',
      currentSemester: 1,
      departmentId: departments[0]?.id || ''
    });
    setIsModalOpen(true);
  };

  const openEditModal = (student) => {
    setEditingStudent(student);
    setIsModalOpen(true);
    setValue('rollNumber', student.rollNumber);
    setValue('firstName', student.firstName);
    setValue('lastName', student.lastName);
    setValue('email', student.email);
    setValue('phone', student.phone);
    setValue('gender', student.gender);
    setValue('currentSemester', student.currentSemester);
    setValue('departmentId', student.departmentId || '');
  };

  const onSubmit = async (data) => {
    try {
      let studentId = editingStudent?.id;
      if (editingStudent) {
        await api.put(`/students/${editingStudent.id}`, data);
      } else {
        const res = await api.post('/students', data);
        studentId = res.data.id;
      }

      // Handle photo upload if present
      if (uploadFile && studentId) {
        const formData = new FormData();
        formData.append('file', uploadFile);
        await api.post(`/students/${studentId}/photo`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
      }

      setIsModalOpen(false);
      setUploadFile(null);
      fetchStudents();
    } catch (err) {
      alert(err.message || 'Operation failed');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this student record?')) return;
    try {
      await api.delete(`/students/${id}`);
      fetchStudents();
    } catch (err) {
      alert(err.message || 'Deletion failed');
    }
  };

  return (
    <div className="animated-fade-in">
      <div className="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-3">
        <div>
          <h2 className="fw-bold gradient-text mb-1">Student Directory</h2>
          <p className="text-secondary mb-0">Manage registered students, information records, and enrollments.</p>
        </div>
        <button className="btn-premium-primary d-flex align-items-center gap-2" onClick={openAddModal}>
          <UserPlus size={18} />
          <span>Register Student</span>
        </button>
      </div>

      {/* Search Filter Bar */}
      <div className="glass-panel p-3 mb-4 d-flex align-items-center gap-3">
        <Search className="text-secondary" size={20} />
        <input 
          type="text" 
          placeholder="Search by name, roll number, or email..." 
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
          className="form-control form-glass border-0 bg-transparent flex-grow-1"
        />
      </div>

      {/* Directory Table */}
      <div className="glass-panel p-3 overflow-x-auto mb-4">
        {loading ? (
          <div className="text-center py-5 text-secondary">Loading student records...</div>
        ) : students.length === 0 ? (
          <div className="text-center py-5 text-secondary">No students registered yet.</div>
        ) : (
          <table className="table-glass">
            <thead>
              <tr>
                <th>Profile</th>
                <th>Roll Number</th>
                <th>Email</th>
                <th>Semester</th>
                <th>Department</th>
                <th>CGPA</th>
                <th className="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              {students.map((student) => (
                <tr key={student.id}>
                  <td>
                    <div className="d-flex align-items-center gap-3">
                      <div className="avatar-placeholder rounded-circle bg-secondary d-flex align-items-center justify-content-center text-info fw-bold" style={{ width: '40px', height: '40px' }}>
                        {student.photoUrl ? (
                          <img src={`http://localhost:8080${student.photoUrl}`} alt="" className="w-100 h-100 rounded-circle object-fit-cover" />
                        ) : (
                          student.firstName.charAt(0) + student.lastName.charAt(0)
                        )}
                      </div>
                      <span className="fw-semibold">{student.fullName}</span>
                    </div>
                  </td>
                  <td><code>{student.rollNumber}</code></td>
                  <td>{student.email}</td>
                  <td>Sem {student.currentSemester}</td>
                  <td>{student.departmentName || 'N/A'}</td>
                  <td><span className="badge badge-custom bg-secondary text-info">{student.cgpa?.toFixed(2) || '0.00'}</span></td>
                  <td className="text-end">
                    <div className="d-flex gap-2 justify-content-end">
                      <button className="btn btn-sm btn-outline-info" onClick={() => openEditModal(student)}>
                        <Edit2 size={14} />
                      </button>
                      <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(student.id)}>
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="d-flex justify-content-center gap-2">
          <button 
            disabled={page === 0} 
            onClick={() => setPage(page - 1)}
            className="btn btn-sm btn-outline-secondary"
          >
            Prev
          </button>
          <span className="align-self-center text-secondary small">Page {page + 1} of {totalPages}</span>
          <button 
            disabled={page === totalPages - 1} 
            onClick={() => setPage(page + 1)}
            className="btn btn-sm btn-outline-secondary"
          >
            Next
          </button>
        </div>
      )}

      {/* Registration/Edit Modal */}
      {isModalOpen && (
        <div className="modal-backdrop show d-flex align-items-center justify-content-center p-3" style={{ background: 'rgba(0,0,0,0.8)' }}>
          <div className="glass-panel p-4 w-100 animated-fade-in" style={{ maxWidth: '600px' }}>
            <div className="d-flex align-items-center justify-content-between mb-4 border-bottom border-secondary pb-3">
              <h5 className="fw-bold mb-0">{editingStudent ? 'Edit Student Details' : 'Register New Student'}</h5>
              <button className="btn btn-link text-secondary p-0" onClick={() => setIsModalOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)}>
              <div className="row g-3 mb-4">
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">First Name</label>
                  <input type="text" className="form-control form-glass" {...register('firstName', { required: true })} />
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Last Name</label>
                  <input type="text" className="form-control form-glass" {...register('lastName', { required: true })} />
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Roll Number</label>
                  <input type="text" className="form-control form-glass" {...register('rollNumber', { required: true })} />
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Email Address</label>
                  <input type="email" className="form-control form-glass" {...register('email', { required: true })} />
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Phone Number</label>
                  <input type="text" className="form-control form-glass" {...register('phone')} />
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Gender</label>
                  <select className="form-select form-glass" {...register('gender')}>
                    <option value="MALE">Male</option>
                    <option value="FEMALE">Female</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Department</label>
                  <select className="form-select form-glass" {...register('departmentId', { required: true })}>
                    {departments.map((dept) => (
                      <option key={dept.id} value={dept.id}>{dept.name}</option>
                    ))}
                  </select>
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Current Semester</label>
                  <input type="number" className="form-control form-glass" {...register('currentSemester', { valueAsNumber: true })} />
                </div>
                <div className="col-12">
                  <label className="form-label text-secondary small">Upload Profile Photo</label>
                  <div className="d-flex align-items-center gap-3">
                    <input 
                      type="file" 
                      id="photo-input" 
                      onChange={(e) => setUploadFile(e.target.files[0])}
                      className="d-none" 
                    />
                    <label htmlFor="photo-input" className="btn btn-outline-info d-flex align-items-center gap-2 mb-0 cursor-pointer">
                      <Upload size={16} />
                      <span>{uploadFile ? uploadFile.name : 'Choose File'}</span>
                    </label>
                  </div>
                </div>
              </div>

              <div className="d-flex justify-content-end gap-2">
                <button type="button" className="btn btn-premium-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn-premium-primary">Save Record</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Students;
