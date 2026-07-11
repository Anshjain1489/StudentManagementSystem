ackage in.springproject.service.impl;

import in.springproject.dto.attendance.*;
import in.springproject.entity.*;
import in.springproject.entity.enums.AttendanceStatus;
import in.springproject.exception.*;
import in.springproject.repository.*;
import in.springproject.service.AttendanceService;
import in.springproject.util.PageResponse;
import in.springproject.util.QrCodeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AttendanceService}.
 * Handles attendance marking (individual/bulk), updates, queries, summaries,
 * and QR-code generation for course attendance sessions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final QrCodeUtil qrCodeUtil;

    @Override
    public AttendanceResponse markAttendance(AttendanceRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        // Guard against duplicate attendance records for the same day
        attendanceRepository.findByStudentIdAndCourseIdAndDate(
            request.getStudentId(), request.getCourseId(), request.getDate())
            .ifPresent(a -> {
                throw new DuplicateResourceException("Attendance already marked for this student on this date");
            });

        Attendance attendance = Attendance.builder()
            .student(student)
            .course(course)
            .date(request.getDate())
            .status(request.getStatus())
            .remarks(request.getRemarks())
            .qrVerified(Boolean.TRUE.equals(request.getQrVerified()))
            .build();

        return mapToResponse(attendanceRepository.save(attendance));
    }

    @Override
    public List<AttendanceResponse> markBulkAttendance(List<AttendanceRequest> requests) {
        return requests.stream()
            .map(this::markAttendance)
            .collect(Collectors.toList());
    }

    @Override
    public AttendanceResponse updateAttendance(Long id, AttendanceRequest request) {
        Attendance attendance = attendanceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));
        attendance.setStatus(request.getStatus());
        attendance.setRemarks(request.getRemarks());
        return mapToResponse(attendanceRepository.save(attendance));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getAttendanceByDateAndCourse(LocalDate date, Long courseId) {
        return attendanceRepository.findByCourseAndDate(courseId, date)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponse> getStudentAttendanceHistory(Long studentId, LocalDate startDate, LocalDate endDate) {
        return attendanceRepository.findByStudentAndDateRange(studentId, startDate, endDate)
            .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSummaryResponse getAttendanceSummary(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        long total   = attendanceRepository.countTotalByStudentAndCourse(studentId, courseId);
        long present = attendanceRepository.countByStudentAndCourseAndStatus(studentId, courseId, AttendanceStatus.PRESENT);
        long absent  = attendanceRepository.countByStudentAndCourseAndStatus(studentId, courseId, AttendanceStatus.ABSENT);
        long late    = attendanceRepository.countByStudentAndCourseAndStatus(studentId, courseId, AttendanceStatus.LATE);

        double percentage = total > 0 ? ((double) (present + late) / total) * 100.0 : 0.0;
        String status = percentage >= 75 ? "GOOD" : percentage >= 60 ? "WARNING" : "CRITICAL";

        return AttendanceSummaryResponse.builder()
            .studentId(studentId)
            .studentName(student.getFirstName() + " " + student.getLastName())
            .courseId(courseId)
            .courseName(course.getName())
            .totalClasses(total)
            .presentCount(present)
            .absentCount(absent)
            .lateCount(late)
            .attendancePercentage(Math.round(percentage * 100.0) / 100.0)
            .status(status)
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<AttendanceResponse> getStudentAttendance(Long studentId, Pageable pageable) {
        Page<AttendanceResponse> page = attendanceRepository.findByStudentId(studentId, pageable)
            .map(this::mapToResponse);
        return PageResponse.from(page);
    }

    @Override
    public String generateQrCode(Long courseId, LocalDate date) {
        String data = "course:" + courseId + "|date:" + date.toString();
        return qrCodeUtil.generateQrCodeBase64(data);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private AttendanceResponse mapToResponse(Attendance a) {
        return AttendanceResponse.builder()
            .id(a.getId())
            .studentId(a.getStudent().getId())
            .studentName(a.getStudent().getFirstName() + " " + a.getStudent().getLastName())
            .rollNumber(a.getStudent().getRollNumber())
            .courseId(a.getCourse().getId())
            .courseName(a.getCourse().getName())
            .date(a.getDate())
            .status(a.getStatus())
            .remarks(a.getRemarks())
            .qrVerified(a.getQrVerified())
            .createdAt(a.getCreatedAt())
            .build();
    }
}
