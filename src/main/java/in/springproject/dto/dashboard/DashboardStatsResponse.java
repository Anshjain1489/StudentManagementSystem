package in.springproject.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO aggregating key metrics for the admin dashboard.
 * Provides counts, financial totals, academic averages, and recent activity stats
 * in a single response to minimize API round-trips on the dashboard page.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {

    // --- Entity Counts ---
    private long totalStudents;
    private long totalTeachers;
    private long totalCourses;
    private long totalDepartments;

    // --- Attendance ---
    private double averageAttendancePercentage;

    // --- Financial ---
    private BigDecimal totalFeesCollected;
    private BigDecimal totalPendingFees;

    // --- Exams ---
    private long totalExams;
    private double averageGpa;

    // --- Recent Activity ---
    private long newStudentsThisMonth;
    private long pendingFeesCount;
    private long upcomingExams;
    private long unreadNotifications;
}
