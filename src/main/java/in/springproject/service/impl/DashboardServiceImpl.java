package in.springproject.service.impl;

import in.springproject.dto.dashboard.DashboardStatsResponse;
import in.springproject.repository.*;
import in.springproject.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Implementation of {@link DashboardService}.
 * Aggregates stats from all modules for role-specific dashboards.
 * Results cached for performance.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final FeesRepository feesRepository;
    private final PaymentRepository paymentRepository;
    private final ExamRepository examRepository;

    @Override
    @Cacheable(value = "dashboardStats", key = "'admin'")
    public DashboardStatsResponse getAdminDashboardStats() {
        long totalStudents   = studentRepository.countActive();
        long totalTeachers   = teacherRepository.countActive();
        long totalCourses    = courseRepository.countActive();
        long totalDepts      = departmentRepository.countActive();
        long pendingCount    = feesRepository.countPending();

        BigDecimal collected = paymentRepository.findTotalCollected();

        return DashboardStatsResponse.builder()
            .totalStudents(totalStudents)
            .totalTeachers(totalTeachers)
            .totalCourses(totalCourses)
            .totalDepartments(totalDepts)
            .totalFeesCollected(collected != null ? collected : BigDecimal.ZERO)
            .totalPendingFees(BigDecimal.ZERO)
            .pendingFeesCount(pendingCount)
            .build();
    }

    @Override
    public DashboardStatsResponse getTeacherDashboardStats(String teacherEmail) {
        return DashboardStatsResponse.builder()
            .totalStudents(studentRepository.countActive())
            .totalCourses(courseRepository.countActive())
            .build();
    }

    @Override
    public DashboardStatsResponse getStudentDashboardStats(String studentEmail) {
        return DashboardStatsResponse.builder()
            .totalCourses(0)
            .build();
    }
}
