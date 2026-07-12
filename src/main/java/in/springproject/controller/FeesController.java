package in.springproject.controller;

import in.springproject.dto.fees.*;
import in.springproject.service.FeesService;
import in.springproject.util.ApiResponse;
import in.springproject.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for fee management and payment processing.
 * Endpoint: /api/v1/fees
 */
@RestController
@RequestMapping("/api/v1/fees")
@RequiredArgsConstructor
@Tag(name = "Fees & Payments", description = "Fee management and payment processing APIs")
@SecurityRequirement(name = "bearerAuth")
public class FeesController {

    private final FeesService feesService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a fee record for a student")
    public ResponseEntity<ApiResponse<FeesResponse>> create(@Valid @RequestBody FeesRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Fee record created", feesService.createFees(request), 201));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all fee records (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<FeesResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Fees retrieved",
            feesService.getAllFees(PageRequest.of(page, size, Sort.by("createdAt").descending()))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get fee record by ID")
    public ResponseEntity<ApiResponse<FeesResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Fee retrieved", feesService.getFeesById(id)));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get all fees for a student")
    public ResponseEntity<ApiResponse<List<FeesResponse>>> getStudentFees(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Fees retrieved", feesService.getStudentFees(studentId)));
    }

    @GetMapping("/student/{studentId}/pending")
    @Operation(summary = "Get pending fees for a student")
    public ResponseEntity<ApiResponse<List<FeesResponse>>> getPendingFees(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Pending fees retrieved",
            feesService.getPendingFees(studentId)));
    }

    @GetMapping("/student/{studentId}/pending-amount")
    @Operation(summary = "Get total pending amount for a student")
    public ResponseEntity<ApiResponse<BigDecimal>> getPendingAmount(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Pending amount", feesService.getPendingAmount(studentId)));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Process a fee payment and generate receipt")
    public ResponseEntity<ApiResponse<PaymentResponse>> processPayment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Payment processed", feesService.processPayment(request), 201));
    }

    @GetMapping("/payments/student/{studentId}")
    @Operation(summary = "Get payment history for a student")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getPaymentHistory(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Payment history retrieved",
            feesService.getPaymentHistory(studentId)));
    }

    @GetMapping("/payments/total-collected")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get total fees collected across all students")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalCollected() {
        return ResponseEntity.ok(ApiResponse.success("Total collected", feesService.getTotalCollected()));
    }
}
