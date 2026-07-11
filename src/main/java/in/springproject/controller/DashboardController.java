ackage in.springproject.controller;

import in.springproject.dto.dashboard.DashboardStatsResponse;
import in.springproject.service.DashboardService;
import in.springproject.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for dashboard statistics.
 * Endpoint: /api/v1/dashboard
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics and analytics APIs")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getAdminStats() {
        return ResponseEntity.ok(ApiResponse.success("Dashboard stats retrieved",
            dashboardService.getAdminDashboardStats()));
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get teacher dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getTeacherStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Teacher stats retrieved",
            dashboardService.getTeacherDashboardStats(userDetails.getUsername())));
    }

    @GetMapping("/student")
    @Operation(summary = "Get student dashboard statistics")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStudentStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success("Student stats retrieved",
            dashboardService.getStudentDashboardStats(userDetails.getUsername())));
    }
}
