import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { useForm } from 'react-hook-form';
import { BookOpen, Plus, Edit2, Trash2, X, GraduationCap } from 'lucide-react';

const Courses = () => {
  const [courses, setCourses] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [teachers, setTeachers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingCourse, setEditingCourse] = useState(null);
  
  const [isAssignOpen, setIsAssignOpen] = useState(false);
  const [selectedCourse, setSelectedCourse] = useState(null);

  const { register, handleSubmit, reset, setValue } = useForm();
  const assignForm = useForm();

  const fetchCourses = async () => {
    try {
      setLoading(true);
      const res = await api.get('/courses');
      if (res.success) {
        setCourses(res.data.content || []);
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
      if (res.success) setDepartments(res.data || []);
    } catch (err) {
      console.error(err);
    }
  };

  const fetchTeachers = async () => {
    try {
      const res = await api.get('/teachers');
      if (res.success) setTeachers(res.data.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchCourses();
    fetchDepartments();
    fetchTeachers();
  }, []);

  const openAddModal = () => {
    setEditingCourse(null);
    reset({
      code: '',
      name: '',
      description: '',
      credits: 3,
      maxStudents: 60,
      departmentId: departments[0]?.id || ''
    });
    setIsModalOpen(true);
  };

  const openEditModal = (course) => {
    setEditingCourse(course);
    setIsModalOpen(true);
    setValue('code', course.code);
    setValue('name', course.name);
    setValue('description', course.description);
    setValue('credits', course.credits);
    setValue('maxStudents', course.maxStudents);
    setValue('departmentId', course.departmentId || '');
  };

  const openAssignModal = (course) => {
    setSelectedCourse(course);
    setIsAssignOpen(true);
    assignForm.reset({
      teacherId: teachers[0]?.id || ''
    });
  };

  const onSubmit = async (data) => {
    try {
      if (editingCourse) {
        await api.put(`/courses/${editingCourse.id}`, data);
      } else {
        await api.post('/courses', data);
      }
      setIsModalOpen(false);
      fetchCourses();
    } catch (err) {
      alert(err.message || 'Operation failed');
    }
  };

  const handleAssignSubmit = async (data) => {
    try {
      await api.post(`/courses/${selectedCourse.id}/teachers/${data.teacherId}`);
      setIsAssignOpen(false);
      alert('Teacher assigned successfully!');
    } catch (err) {
      alert(err.message || 'Assignment failed');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this course?')) return;
    try {
      await api.delete(`/courses/${id}`);
      fetchCourses();
    } catch (err) {
      alert(err.message || 'Deletion failed');
    }
  };

  return (
    <div className="animated-fade-in">
      <div className="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-3">
        <div>
          <h2 className="fw-bold gradient-text mb-1">Course Catalog</h2>
          <p className="text-secondary mb-0">Define, schedule, and assign academic course curricula across university divisions.</p>
        </div>
        <button className="btn-premium-primary d-flex align-items-center gap-2" onClick={openAddModal}>
          <Plus size={18} />
          <span>New Course</span>
        </button>
      </div>

      {loading ? (
        <div className="text-center py-5 text-secondary">Loading courses...</div>
      ) : courses.length === 0 ? (
        <div className="text-center py-5 text-secondary">No courses created yet.</div>
      ) : (
        <div className="row g-4">
          {courses.map((course) => (
            <div key={course.id} className="col-12 col-md-6 col-xxl-4">
              <div className="glass-panel p-4 h-100 d-flex flex-column justify-content-between">
                <div>
                  <div className="d-flex align-items-center justify-content-between mb-3">
                    <span className="badge badge-custom bg-secondary text-info fw-bold">{course.code}</span>
                    <span className="small text-secondary">{course.credits} Credits</span>
                  </div>
                  <h5 className="fw-bold mb-2">{course.name}</h5>
                  <p className="text-secondary small mb-3 text-truncate-2" style={{ height: '40px', overflow: 'hidden' }}>
                    {course.description || 'No course description has been added yet.'}
                  </p>
                  <div className="d-flex justify-content-between text-secondary small border-top border-secondary pt-3 mb-4">
                    <span>Dept: <strong>{course.departmentName || 'N/A'}</strong></span>
                    <span>Max Size: <strong>{course.maxStudents || 60}</strong></span>
                  </div>
                </div>

                <div className="d-flex justify-content-between gap-2">
                  <button className="btn btn-sm btn-outline-info d-flex align-items-center gap-2" onClick={() => openAssignModal(course)}>
                    <GraduationCap size={14} />
                    <span>Assign Prof</span>
                  </button>
                  <div className="d-flex gap-2">
                    <button className="btn btn-sm btn-outline-secondary" onClick={() => openEditModal(course)}>
                      <Edit2 size={14} />
                    </button>
                    <button className="btn btn-sm btn-outline-danger" onClick={() => handleDelete(course.id)}>
                      <Trash2 size={14} />
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Course Modal */}
      {isModalOpen && (
        <div className="modal-backdrop show d-flex align-items-center justify-content-center p-3" style={{ background: 'rgba(0,0,0,0.8)' }}>
          <div className="glass-panel p-4 w-100 animated-fade-in" style={{ maxWidth: '500px' }}>
            <div className="d-flex align-items-center justify-content-between mb-4 border-bottom border-secondary pb-3">
              <h5 className="fw-bold mb-0">{editingCourse ? 'Update Course Details' : 'Create New Course'}</h5>
              <button className="btn btn-link text-secondary p-0" onClick={() => setIsModalOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit(onSubmit)}>
              <div className="mb-3">
                <label className="form-label text-secondary small">Course Code</label>
                <input type="text" placeholder="e.g. CS-101" className="form-control form-glass" {...register('code', { required: true })} />
              </div>
              <div className="mb-3">
                <label className="form-label text-secondary small">Course Title</label>
                <input type="text" placeholder="e.g. Intro to Computer Science" className="form-control form-glass" {...register('name', { required: true })} />
              </div>
              <div className="mb-3">
                <label className="form-label text-secondary small">Description</label>
                <textarea rows="3" className="form-control form-glass" {...register('description')}></textarea>
              </div>
              <div className="row g-3 mb-4">
                <div className="col-6">
                  <label className="form-label text-secondary small">Credits</label>
                  <input type="number" className="form-control form-glass" {...register('credits', { valueAsNumber: true })} />
                </div>
                <div className="col-6">
                  <label className="form-label text-secondary small">Max Capacity</label>
                  <input type="number" className="form-control form-glass" {...register('maxStudents', { valueAsNumber: true })} />
                </div>
                <div className="col-12">
                  <label className="form-label text-secondary small">Offering Department</label>
                  <select className="form-select form-glass" {...register('departmentId', { required: true })}>
                    {departments.map((dept) => (
                      <option key={dept.id} value={dept.id}>{dept.name}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="d-flex justify-content-end gap-2">
                <button type="button" className="btn btn-premium-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn-premium-primary">Save Course</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Assign Teacher Modal */}
      {isAssignOpen && (
        <div className="modal-backdrop show d-flex align-items-center justify-content-center p-3" style={{ background: 'rgba(0,0,0,0.8)' }}>
          <div className="glass-panel p-4 w-100 animated-fade-in" style={{ maxWidth: '400px' }}>
            <div className="d-flex align-items-center justify-content-between mb-4 border-bottom border-secondary pb-3">
              <h5 className="fw-bold mb-0">Assign Professor</h5>
              <button className="btn btn-link text-secondary p-0" onClick={() => setIsAssignOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={assignForm.handleSubmit(handleAssignSubmit)}>
              <div className="mb-4">
                <label className="form-label text-secondary small">Select Instructor</label>
                <select className="form-select form-glass" {...assignForm.register('teacherId', { required: true })}>
                  {teachers.map((teacher) => (
                    <option key={teacher.id} value={teacher.id}>{teacher.fullName} ({teacher.employeeId})</option>
                  ))}
                </select>
              </div>

              <div className="d-flex justify-content-end gap-2">
                <button type="button" className="btn btn-premium-secondary" onClick={() => setIsAssignOpen(false)}>Cancel</button>
                <button type="submit" className="btn-premium-primary">Assign Role</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Courses;
