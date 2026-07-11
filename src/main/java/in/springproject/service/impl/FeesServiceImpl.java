ackage in.springproject.service.impl;

import in.springproject.dto.fees.*;
import in.springproject.entity.*;
import in.springproject.entity.enums.PaymentStatus;
import in.springproject.exception.*;
import in.springproject.repository.*;
import in.springproject.service.FeesService;
import in.springproject.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link FeesService}.
 * Handles fee record creation, payment processing, and financial reporting.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeesServiceImpl implements FeesService {

    private final FeesRepository feesRepository;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;

    @Override
    public FeesResponse createFees(FeesRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));

        Fees fees = Fees.builder()
            .student(student)
            .feeType(request.getFeeType())
            .amount(request.getAmount())
            .dueDate(request.getDueDate())
            .academicYear(request.getAcademicYear())
            .semester(request.getSemester())
            .description(request.getDescription())
            .build();

        return mapFeesToResponse(feesRepository.save(fees));
    }

    @Override
    @Transactional(readOnly = true)
    public FeesResponse getFeesById(Long id) {
        return mapFeesToResponse(feesRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Fees", "id", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeesResponse> getStudentFees(Long studentId) {
        return feesRepository.findByStudentId(studentId).stream()
            .map(this::mapFeesToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeesResponse> getPendingFees(Long studentId) {
        return feesRepository.findByStudentIdAndIsPaid(studentId, false).stream()
            .map(this::mapFeesToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<FeesResponse> getAllFees(Pageable pageable) {
        Page<FeesResponse> page = feesRepository.findAllActive(pageable).map(this::mapFeesToResponse);
        return PageResponse.from(page);
    }

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        Fees fees = feesRepository.findById(request.getFeesId())
            .orElseThrow(() -> new ResourceNotFoundException("Fees", "id", request.getFeesId()));

        if (Boolean.TRUE.equals(fees.getIsPaid())) {
            throw new BadRequestException("This fee has already been fully paid");
        }

        String receiptNumber = "RCP-" + System.currentTimeMillis() + "-"
            + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = Payment.builder()
            .fees(fees)
            .amount(request.getAmount())
            .paymentDate(LocalDateTime.now())
            .paymentMethod(request.getPaymentMethod())
            .status(PaymentStatus.COMPLETED)
            .receiptNumber(receiptNumber)
            .transactionId(request.getTransactionId())
            .remarks(request.getRemarks())
            .build();

        fees.setIsPaid(true);
        feesRepository.save(fees);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment processed: {} for fees ID: {}", receiptNumber, fees.getId());
        return mapPaymentToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentHistory(Long studentId) {
        return paymentRepository.findByFeesStudentId(studentId).stream()
            .map(this::mapPaymentToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalCollected() {
        BigDecimal total = paymentRepository.findTotalCollected();
        return total != null ? total : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getPendingAmount(Long studentId) {
        BigDecimal pending = feesRepository.findPendingAmountByStudent(studentId);
        return pending != null ? pending : BigDecimal.ZERO;
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingFees() {
        return feesRepository.countPending();
    }

    // ─── Mappers ───────────────────────────────────────────────────────────────

    private FeesResponse mapFeesToResponse(Fees f) {
        return FeesResponse.builder()
            .id(f.getId())
            .studentId(f.getStudent().getId())
            .studentName(f.getStudent().getFirstName() + " " + f.getStudent().getLastName())
            .rollNumber(f.getStudent().getRollNumber())
            .feeType(f.getFeeType()).amount(f.getAmount())
            .dueDate(f.getDueDate()).academicYear(f.getAcademicYear())
            .semester(f.getSemester()).description(f.getDescription())
            .isPaid(f.getIsPaid()).createdAt(f.getCreatedAt())
            .build();
    }

    private PaymentResponse mapPaymentToResponse(Payment p) {
        return PaymentResponse.builder()
            .id(p.getId()).feesId(p.getFees().getId())
            .studentName(p.getFees().getStudent().getFirstName() + " " + p.getFees().getStudent().getLastName())
            .amount(p.getAmount()).paymentDate(p.getPaymentDate())
            .paymentMethod(p.getPaymentMethod()).status(p.getStatus())
            .receiptNumber(p.getReceiptNumber()).transactionId(p.getTransactionId())
            .remarks(p.getRemarks())
            .build();
    }
}
