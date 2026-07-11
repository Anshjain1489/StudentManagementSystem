ackage in.springproject.controller;

import in.springproject.dto.attendance.*;
import in.springproject.service.AttendanceService;
import in.springproject.util.ApiResponse;
import in.springproject.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for attendance management.
 * Base path: /api/v1/attendance
 */
@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management APIs")
@SecurityRequirement(name = "bearerAuth")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Mark attendance for a single student")
    public ResponseEntity<ApiResponse<AttendanceResponse>> mark(@Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attendance marked", attendanceService.markAttendance(request)));
    }

    @PostMapping("/bulk")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Mark attendance for multiple students at once")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> markBulk(
            @Valid @RequestBody List<AttendanceRequest> requests) {
        return ResponseEntity.ok(ApiResponse.success("Bulk attendance marked", attendanceService.markBulkAttendance(requests)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update an existing attendance record")
    public ResponseEntity<ApiResponse<AttendanceResponse>> update(
            @PathVariable Long id, @Valid @RequestBody AttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Attendance updated", attendanceService.updateAttendance(id, request)));
    }

    @GetMapping("/course/{courseId}/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get attendance for a course on a specific date")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getByDateAndCourse(
            @PathVariable Long courseId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved",
            attendanceService.getAttendanceByDateAndCourse(date, courseId)));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get paginated attendance records for a student")
    public ResponseEntity<ApiResponse<PageResponse<AttendanceResponse>>> getStudentAttendance(
            @PathVariable Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved",
            attendanceService.getStudentAttendance(studentId,
                PageRequest.of(page, size, Sort.by("date").descending()))));
    }

    @GetMapping("/student/{studentId}/history")
    @Operation(summary = "Get student attendance history between two dates")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getHistory(
            @PathVariable Long studentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success("History retrieved",
            attendanceService.getStudentAttendanceHistory(studentId, startDate, endDate)));
    }

    @GetMapping("/student/{studentId}/summary/{courseId}")
    @Operation(summary = "Get attendance percentage summary for a student in a course")
    public ResponseEntity<ApiResponse<AttendanceSummaryResponse>> getSummary(
            @PathVariable Long studentId, @PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success("Summary retrieved",
            attendanceService.getAttendanceSummary(studentId, courseId)));
    }

    @GetMapping("/qr/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Generate a QR code for attendance marking (returns Base64 PNG)")
    public ResponseEntity<ApiResponse<String>> generateQr(
            @PathVariable Long courseId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LocalDate attendanceDate = date != null ? date : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success("QR code generated",
            attendanceService.generateQrCode(courseId, attendanceDate)));
    }
}
