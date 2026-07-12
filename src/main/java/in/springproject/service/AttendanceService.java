package in.springproject.service;

import in.springproject.dto.attendance.*;
import in.springproject.util.PageResponse;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing student attendance.
 * Supports individual and bulk marking, summaries, and QR-code generation.
 */
public interface AttendanceService {
    AttendanceResponse markAttendance(AttendanceRequest request);
    List<AttendanceResponse> markBulkAttendance(List<AttendanceRequest> requests);
    AttendanceResponse updateAttendance(Long id, AttendanceRequest request);
    List<AttendanceResponse> getAttendanceByDateAndCourse(LocalDate date, Long courseId);
    List<AttendanceResponse> getStudentAttendanceHistory(Long studentId, LocalDate startDate, LocalDate endDate);
    AttendanceSummaryResponse getAttendanceSummary(Long studentId, Long courseId);
    PageResponse<AttendanceResponse> getStudentAttendance(Long studentId, Pageable pageable);
    String generateQrCode(Long courseId, LocalDate date);
}
