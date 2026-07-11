ackage in.springproject.dto.attendance;

import in.springproject.entity.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO representing a single attendance record returned by API responses.
 * Includes denormalized student and course information for display convenience.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {

    private Long id;
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private Long courseId;
    private String courseName;
    private LocalDate date;
    private AttendanceStatus status;
    private String remarks;
    private Boolean qrVerified;
    private LocalDateTime createdAt;
}
