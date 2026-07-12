package in.springproject.service;

import in.springproject.dto.fees.*;
import in.springproject.util.PageResponse;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for fee management and payment processing.
 */
public interface FeesService {
    FeesResponse createFees(FeesRequest request);
    FeesResponse getFeesById(Long id);
    List<FeesResponse> getStudentFees(Long studentId);
    List<FeesResponse> getPendingFees(Long studentId);
    PageResponse<FeesResponse> getAllFees(Pageable pageable);
    PaymentResponse processPayment(PaymentRequest request);
    List<PaymentResponse> getPaymentHistory(Long studentId);
    BigDecimal getTotalCollected();
    BigDecimal getPendingAmount(Long studentId);
    long countPendingFees();
}
