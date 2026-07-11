import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { useForm } from 'react-hook-form';
import { useAuth } from '../context/AuthContext';
import { CreditCard, DollarSign, Plus, X, Wallet, ShieldCheck } from 'lucide-react';

const Fees = () => {
  const { user, hasRole } = useAuth();
  const [feesList, setFeesList] = useState([]);
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isPaymentOpen, setIsPaymentOpen] = useState(false);
  const [selectedFee, setSelectedFee] = useState(null);

  const { register, handleSubmit, reset } = useForm();
  const paymentForm = useForm();

  const fetchFees = async () => {
    try {
      setLoading(true);
      let res;
      if (hasRole('ROLE_STUDENT')) {
        res = await api.get(`/fees/student/${user.id}`); // mocked student identification
      } else {
        res = await api.get('/fees');
      }
      if (res.success) {
        setFeesList(res.data.content || res.data || []);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const fetchStudents = async () => {
    try {
      const res = await api.get('/students');
      if (res.success) setStudents(res.data.content || []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchFees();
    if (hasRole('ROLE_ADMIN')) {
      fetchStudents();
    }
  }, []);

  const openAddModal = () => {
    reset({
      studentId: students[0]?.id || '',
      feeType: 'TUITION',
      amount: 1500,
      dueDate: new Date().toISOString().split('T')[0],
      academicYear: '2025-2026',
      semester: 'Fall',
      description: 'Fall semester tuition'
    });
    setIsModalOpen(true);
  };

  const openPaymentModal = (fee) => {
    setSelectedFee(fee);
    setIsPaymentOpen(true);
    paymentForm.reset({
      amount: fee.amount,
      paymentMethod: 'ONLINE',
      transactionId: 'TXN-' + Math.random().toString(36).substring(2, 10).toUpperCase(),
      remarks: 'Tuition clearing'
    });
  };

  const onSubmitFee = async (data) => {
    try {
      await api.post('/fees', data);
      setIsModalOpen(false);
      fetchFees();
    } catch (err) {
      alert(err.message || 'Creation failed');
    }
  };

  const onSubmitPayment = async (data) => {
    try {
      await api.post('/fees/payments', {
        feesId: selectedFee.id,
        amount: data.amount,
        paymentMethod: data.paymentMethod,
        transactionId: data.transactionId,
        remarks: data.remarks
      });
      setIsPaymentOpen(false);
      alert('Fee paid successfully! Receipt generated.');
      fetchFees();
    } catch (err) {
      alert(err.message || 'Payment failed');
    }
  };

  return (
    <div className="animated-fade-in">
      <div className="d-flex align-items-center justify-content-between mb-4 flex-wrap gap-3">
        <div>
          <h2 className="fw-bold gradient-text mb-1">Tuition & Billing</h2>
          <p className="text-secondary mb-0">Track outstanding fee balances, invoice payments, and historical receipts.</p>
        </div>
        {hasRole('ROLE_ADMIN') && (
          <button className="btn-premium-primary d-flex align-items-center gap-2" onClick={openAddModal}>
            <Plus size={18} />
            <span>Generate Invoice</span>
          </button>
        )}
      </div>

      {loading ? (
        <div className="text-center py-5 text-secondary">Loading financial statements...</div>
      ) : feesList.length === 0 ? (
        <div className="text-center py-5 text-secondary">No invoices issued.</div>
      ) : (
        <div className="glass-panel p-3 overflow-x-auto">
          <table className="table-glass">
            <thead>
              <tr>
                <th>Student</th>
                <th>Fee Type</th>
                <th>Academic Year</th>
                <th>Semester</th>
                <th>Due Date</th>
                <th>Amount</th>
                <th>Status</th>
                <th className="text-end">Actions</th>
              </tr>
            </thead>
            <tbody>
              {feesList.map((fee) => (
                <tr key={fee.id}>
                  <td>
                    <span className="fw-semibold d-block">{fee.studentName || 'Student'}</span>
                    <small className="text-secondary">{fee.rollNumber || 'CS-Roster'}</small>
                  </td>
                  <td><span className="text-capitalize text-info">{fee.feeType.toLowerCase()}</span></td>
                  <td>{fee.academicYear}</td>
                  <td>{fee.semester}</td>
                  <td><code>{fee.dueDate}</code></td>
                  <td><strong className="text-light">${fee.amount.toFixed(2)}</strong></td>
                  <td>
                    <span className={`badge badge-custom ${fee.isPaid ? 'badge-paid' : 'badge-pending'}`}>
                      {fee.isPaid ? 'Paid' : 'Pending'}
                    </span>
                  </td>
                  <td className="text-end">
                    {!fee.isPaid && (
                      <button className="btn btn-sm btn-outline-info d-flex align-items-center gap-1 ms-auto" onClick={() => openPaymentModal(fee)}>
                        <Wallet size={12} />
                        <span>Pay</span>
                      </button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {/* Invoice Generation Modal */}
      {isModalOpen && (
        <div className="modal-backdrop show d-flex align-items-center justify-content-center p-3" style={{ background: 'rgba(0,0,0,0.8)' }}>
          <div className="glass-panel p-4 w-100 animated-fade-in" style={{ maxWidth: '500px' }}>
            <div className="d-flex align-items-center justify-content-between mb-4 border-bottom border-secondary pb-3">
              <h5 className="fw-bold mb-0">Generate Student Invoice</h5>
              <button className="btn btn-link text-secondary p-0" onClick={() => setIsModalOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <form onSubmit={handleSubmit(onSubmitFee)}>
              <div className="mb-3">
                <label className="form-label text-secondary small">Select Student</label>
                <select className="form-select form-glass" {...register('studentId', { required: true })}>
                  {students.map((s) => (
                    <option key={s.id} value={s.id}>{s.fullName} ({s.rollNumber})</option>
                  ))}
                </select>
              </div>

              <div className="row g-3 mb-4">
                <div className="col-6">
                  <label className="form-label text-secondary small">Fee Type</label>
                  <select className="form-select form-glass" {...register('feeType')}>
                    <option value="TUITION">Tuition</option>
                    <option value="HOSTEL">Hostel</option>
                    <option value="LIBRARY">Library</option>
                    <option value="EXAMINATION">Examination</option>
                    <option value="TRANSPORT">Transport</option>
                  </select>
                </div>
                <div className="col-6">
                  <label className="form-label text-secondary small">Due Date</label>
                  <input type="date" className="form-control form-glass" {...register('dueDate', { required: true })} />
                </div>
                <div className="col-6">
                  <label className="form-label text-secondary small">Amount ($)</label>
                  <input type="number" className="form-control form-glass" {...register('amount', { valueAsNumber: true })} />
                </div>
                <div className="col-6">
                  <label className="form-label text-secondary small">Academic Year</label>
                  <input type="text" className="form-control form-glass" {...register('academicYear')} />
                </div>
                <div className="col-12">
                  <label className="form-label text-secondary small">Invoice Description</label>
                  <input type="text" className="form-control form-glass" {...register('description')} />
                </div>
              </div>

              <div className="d-flex justify-content-end gap-2">
                <button type="button" className="btn btn-premium-secondary" onClick={() => setIsModalOpen(false)}>Cancel</button>
                <button type="submit" className="btn-premium-primary">Create Invoice</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Credit Card Processing Mock Modal */}
      {isPaymentOpen && (
        <div className="modal-backdrop show d-flex align-items-center justify-content-center p-3" style={{ background: 'rgba(0,0,0,0.8)' }}>
          <div className="glass-panel p-4 w-100 animated-fade-in" style={{ maxWidth: '420px' }}>
            <div className="d-flex align-items-center justify-content-between mb-4 border-bottom border-secondary pb-3">
              <h5 className="fw-bold mb-0">Secure Checkout</h5>
              <button className="btn btn-link text-secondary p-0" onClick={() => setIsPaymentOpen(false)}>
                <X size={20} />
              </button>
            </div>

            <div className="mb-4 p-3 bg-secondary bg-opacity-20 rounded-3 text-center border border-secondary">
              <span className="text-secondary small">Total Invoice Amount</span>
              <h2 className="fw-bold text-light mb-0">${selectedFee?.amount?.toFixed(2)}</h2>
            </div>

            <form onSubmit={paymentForm.handleSubmit(onSubmitPayment)}>
              <div className="mb-3">
                <label className="form-label text-secondary small">Card Number</label>
                <div className="position-relative">
                  <input type="text" placeholder="4111 2222 3333 4444" className="form-control form-glass pe-5" required />
                  <CreditCard className="position-absolute end-0 top-50 translate-middle-y text-secondary me-3" size={18} />
                </div>
              </div>

              <div className="row g-3 mb-4">
                <div className="col-6">
                  <label className="form-label text-secondary small">Expiry Date</label>
                  <input type="text" placeholder="MM/YY" className="form-control form-glass" required />
                </div>
                <div className="col-6">
                  <label className="form-label text-secondary small">CVV</label>
                  <input type="password" placeholder="•••" maxLength="3" className="form-control form-glass" required />
                </div>
              </div>

              <div className="d-flex align-items-center gap-2 text-success small mb-4 justify-content-center">
                <ShieldCheck size={16} />
                <span>SSL Encrypted Transaction Mock</span>
              </div>

              <div className="d-flex justify-content-end gap-2">
                <button type="button" className="btn btn-premium-secondary" onClick={() => setIsPaymentOpen(false)}>Cancel</button>
                <button type="submit" className="btn-premium-primary w-50">Process Payment</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Fees;
