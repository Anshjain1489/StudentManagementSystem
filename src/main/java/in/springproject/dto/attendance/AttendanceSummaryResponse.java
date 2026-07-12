package in.springproject.dto.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing an aggregated attendance summary for a student in a course.
 * The {@code status} field is a human-readable classification:
 * <ul>
 *   <li>GOOD    – attendance &ge; 75%</li>
 *   <li>WARNING – attendance between 60% and 74%</li>
 *   <li>CRITICAL – attendance &lt; 60%</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceSummaryResponse {

    private Long studentId;
    private String studentName;
    private Long courseId;
    private String courseName;
    private long totalClasses;
    private long presentCount;
    private long absentCount;
    private long lateCount;
    private double attendancePercentage;

    /** GOOD (>=75%), WARNING (60-75%), or CRITICAL (<60%). */
    private String status;
}
