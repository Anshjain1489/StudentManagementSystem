import React, { useEffect, useState } from 'react';
import api, { getFileUrl } from '../services/api';
import { useForm } from 'react-hook-form';
import { Search, UserPlus, Edit2, Trash2, X } from 'lucide-react';

const Teachers = () => {
  const [teachers, setTeachers] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTeacher, setEditingTeacher] = useState(null);

  const { register, handleSubmit, reset, setValue } = useForm();

  const fetchTeachers = async () => {
    try {
      setLoading(true);
      const res = await api.get('/teachers', {
        params: { page, size: 8, search }
      });
      if (res.success && res.data) {
        setTeachers(res.data.content || []);
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
    fetchTeachers();
  }, [page, search]);

  useEffect(() => {
    fetchDepartments();
  }, []);

  const openAddModal = () => {
    setEditingTeacher(null);
    reset({
      employeeId: '',
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      gender: 'MALE',
      qualification: '',
      specialization: '',
      departmentId: departments[0]?.id || ''
    });
    setIsModalOpen(true);
  };

  const openEditModal = (teacher) => {
    setEditingTeacher(teacher);
    setIsModalOpen(true);
    setValue('employeeId', teacher.employeeId);
    setValue('firstName', teacher.firstName);
    setValue('lastName', teacher.lastName);
    setValue('email', teacher.email);
    setValue('phone', teacher.phone);
    setValue('gender', teacher.gender);
    setValue('qualification', teacher.qualification);
    setValue('specialization', teacher.specialization);
    setValue('departmentId', teacher.departmentId || '');
  };

  const onSubmit = async (data) => {
    try {
      if (editingTeacher) {
        await api.put(`/teachers/${editingTeacher.id}`, data);
      } else {
        await api.post('/teachers', data);
      }
      setIsModalOpen(false);
      fetchTeachers();
    } catch (err) {
      alert(err.message || 'Operation failed');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this teacher record?')) return;
    try {
      await api.delete(`/teachers/${id}`);
      fetchTeachers();
    } catch (err) {
      alert(err.message || 'Deletion failed');
    }
  };

  return (
    <div className="animated-fade-in">
      <div className="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-3">
        <div>
          <h2 className="fw-bold gradient-text mb-1">Faculty Directory</h2>
          <p className="text-secondary mb-0">Manage registered faculty professors, academic qualifications, and teaching roles.</p>
        </div>
        <button className="btn-premium-primary d-flex align-items-center gap-2" onClick={openAddModal}>
          <UserPlus size={18} />
          <span>Register Faculty</span>
        </button>
      </div>

      {/* Search Filter Bar */}
      <div className="glass-panel p-3 mb-4 d-flex align-items-center gap-3">
        <Search className="text-secondary" size={20} />
        <input 
          type="text" 
          placeholder="Search by name, employee ID, or email..." 
          value={search}
          onChange={(e) => { setSearch(e.target.value); setPage(0); }}
          className="form-control form-glass border-0 bg-transparent flex-grow-1"
        />
      </div>

      {/* Directory Table */}
      <div className="glass-panel p-3 overflow-x-auto mb-4">
        {loading ? (
          <div className="text-center py-5 text-secondary">Loading faculty records...</div>
        ) : teachers.length === 0 ? (
          <div className="text-center py-5 text-secondary">No faculty members registered yet.</div>
        ) : (
          <table className="table-glass">
            <thead>
              <tr>
                <th>Profile Name</th>
                <th>Employee ID</th>
                <th>Email</th>
                <th>Qualification</th>
                <th>Specialization</th>
                <th>Department</th>
                <th className="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              {teachers.map((teacher) => (
                <tr key={teacher.id}>
                  <td>
                    <div className="d-flex align-items-center gap-3">
                      <div className="avatar-placeholder rounded-circle bg-secondary d-flex align-items-center justify-content-center text-info fw-bold" style={{ width: '40px', height: '40px' }}>
                        {teacher.photoUrl ? (
                          <img src={getFileUrl(teacher.photoUrl)} alt="" className="w-100 h-100 rounded-circle object-fit-cover" />
                        ) : (
                          teacher.firstName.charAt(0) + teacher.lastName.charAt(0)
                        )}
                      </div>
                      <span className="fw-semibold">{teacher.fullName}</span>
                    </div>
                  </td>
                  <td><code>{teacher.employeeId}</code></td>
                  <td>{teacher.email}</td>
                  <td>{teacher.qualification || 'N/A'}</td>
                  <td>{teacher.specialization || 'N/A'}</td>
                  <td>{teacher.departmentName || 'N/A'}</td>
                  <td className="text-end">
                    <div className="d-flex gap-2 justify-content-end">
                      <button className="btn btn-sm btn-outline-info" onClick={() => openEditModal(teacher)}>
                        <Edit2 size={14} />
                      </button>
                      <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(teacher.id)}>
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
              <h5 className="fw-bold mb-0">{editingTeacher ? 'Edit Faculty Details' : 'Register New Faculty'}</h5>
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
                  <label className="form-label text-secondary small">Employee ID</label>
                  <input type="text" className="form-control form-glass" {...register('employeeId', { required: true })} />
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
                  <label className="form-label text-secondary small">Qualification</label>
                  <input type="text" className="form-control form-glass" {...register('qualification')} />
                </div>
                <div className="col-12 col-sm-6">
                  <label className="form-label text-secondary small">Specialization</label>
                  <input type="text" className="form-control form-glass" {...register('specialization')} />
                </div>
                <div className="col-12">
                  <label className="form-label text-secondary small">Department</label>
                  <select className="form-select form-glass" {...register('departmentId', { required: true })}>
                    {departments.map((dept) => (
                      <option key={dept.id} value={dept.id}>{dept.name}</option>
                    ))}
                  </select>
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

export default Teachers;
