package in.springproject.dto.attendance;

import in.springproject.entity.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for marking or updating a student's attendance for a specific course and date.
 * Supports optional QR-code verification flag.
 */
@Data
public class AttendanceRequest {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Status is required")
    private AttendanceStatus status;

    private String remarks;

    /** Indicates whether attendance was verified via QR code scan. */
    private Boolean qrVerified;
}
