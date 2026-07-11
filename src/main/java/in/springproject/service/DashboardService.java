package in.springproject.service;

import in.springproject.dto.dashboard.DashboardStatsResponse;

/**
 * Service interface for dashboard statistics and summary data.
 */
public interface DashboardService {
    DashboardStatsResponse getAdminDashboardStats();
    DashboardStatsResponse getTeacherDashboardStats(String teacherEmail);
    DashboardStatsResponse getStudentDashboardStats(String studentEmail);
}
