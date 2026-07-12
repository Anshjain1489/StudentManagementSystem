package in.springproject.service.impl;

import in.springproject.dto.exam.*;
import in.springproject.entity.*;
import in.springproject.exception.*;
import in.springproject.repository.*;
import in.springproject.service.ExamService;
import in.springproject.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ExamService}.
 * Handles exam lifecycle and automatic grade/GPA calculation using a 4.0 scale.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final ResultRepository resultRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;

    @Override
    public ExamResponse createExam(ExamRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        Exam.ExamBuilder builder = Exam.builder()
            .name(request.getName())
            .examDate(request.getExamDate())
            .startTime(request.getStartTime())
            .endTime(request.getEndTime())
            .totalMarks(request.getTotalMarks())
            .passingMarks(request.getPassingMarks())
            .description(request.getDescription())
            .course(course);

        if (request.getSemesterId() != null) {
            semesterRepository.findById(request.getSemesterId()).ifPresent(builder::semester);
        }
        if (request.getClassroomId() != null) {
            classroomRepository.findById(request.getClassroomId()).ifPresent(builder::classroom);
        }

        Exam saved = examRepository.save(builder.build());
        log.info("Created exam: {} for course: {}", saved.getName(), course.getName());
        return mapExamToResponse(saved);
    }

    @Override
    public ExamResponse updateExam(Long id, ExamRequest request) {
        Exam exam = getExamEntityById(id);
        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course", "id", request.getCourseId()));

        exam.setName(request.getName());
        exam.setExamDate(request.getExamDate());
        exam.setStartTime(request.getStartTime());
        exam.setEndTime(request.getEndTime());
        exam.setTotalMarks(request.getTotalMarks());
        exam.setPassingMarks(request.getPassingMarks());
        exam.setDescription(request.getDescription());
        exam.setCourse(course);
        return mapExamToResponse(examRepository.save(exam));
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResponse getExamById(Long id) {
        return mapExamToResponse(getExamEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> getAllExams(Pageable pageable) {
        Page<ExamResponse> page = examRepository.findAllActive(pageable).map(this::mapExamToResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsByCourse(Long courseId) {
        return examRepository.findByCourseId(courseId).stream()
            .map(this::mapExamToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponse> getExamsBySemester(Long semesterId) {
        return examRepository.findBySemesterId(semesterId).stream()
            .map(this::mapExamToResponse).collect(Collectors.toList());
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = getExamEntityById(id);
        exam.setDeleted(true);
        examRepository.save(exam);
    }

    @Override
    public ResultResponse enterResult(ResultRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", "id", request.getStudentId()));
        Exam exam = getExamEntityById(request.getExamId());

        resultRepository.findByStudentIdAndExamId(request.getStudentId(), request.getExamId())
            .ifPresent(r -> {
                throw new DuplicateResourceException("Result already entered for this student in this exam");
            });

        if (request.getMarksObtained() > exam.getTotalMarks()) {
            throw new BadRequestException("Marks obtained cannot exceed total marks: " + exam.getTotalMarks());
        }

        Result result = buildResult(student, exam, request.getMarksObtained(), request.getRemarks());
        return mapResultToResponse(resultRepository.save(result));
    }

    @Override
    public ResultResponse updateResult(Long id, ResultRequest request) {
        Result result = resultRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Result", "id", id));
        Exam exam = result.getExam();

        if (request.getMarksObtained() > exam.getTotalMarks()) {
            throw new BadRequestException("Marks obtained cannot exceed total marks: " + exam.getTotalMarks());
        }

        result.setMarksObtained(request.getMarksObtained());
        result.setGrade(calculateGrade(request.getMarksObtained(), exam.getTotalMarks()));
        result.setGpa(calculateGpa(request.getMarksObtained(), exam.getTotalMarks()));
        result.setIsPass(request.getMarksObtained() >= exam.getPassingMarks());
        result.setRemarks(request.getRemarks());
        return mapResultToResponse(resultRepository.save(result));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponse> getResultsByExam(Long examId) {
        return resultRepository.findByExamId(examId).stream()
            .map(this::mapResultToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResultResponse> getResultsByStudent(Long studentId) {
        return resultRepository.findByStudentId(studentId).stream()
            .map(this::mapResultToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public double calculateCgpa(Long studentId) {
        Double cgpa = resultRepository.calculateCgpa(studentId);
        return cgpa != null ? Math.round(cgpa * 100.0) / 100.0 : 0.0;
    }

    // ─── Grade/GPA Helpers ─────────────────────────────────────────────────────

    private Result buildResult(Student student, Exam exam, Double marks, String remarks) {
        return Result.builder()
            .student(student).exam(exam)
            .marksObtained(marks)
            .grade(calculateGrade(marks, exam.getTotalMarks()))
            .gpa(calculateGpa(marks, exam.getTotalMarks()))
            .isPass(marks >= exam.getPassingMarks())
            .remarks(remarks)
            .build();
    }

    private String calculateGrade(double marks, int total) {
        double pct = (marks / total) * 100;
        if (pct >= 90) return "A+";
        else if (pct >= 80) return "A";
        else if (pct >= 70) return "B+";
        else if (pct >= 60) return "B";
        else if (pct >= 50) return "C";
        else if (pct >= 40) return "D";
        else return "F";
    }

    private Double calculateGpa(double marks, int total) {
        double pct = (marks / total) * 100;
        if (pct >= 90) return 4.0;
        else if (pct >= 80) return 3.7;
        else if (pct >= 70) return 3.3;
        else if (pct >= 60) return 3.0;
        else if (pct >= 50) return 2.0;
        else if (pct >= 40) return 1.0;
        else return 0.0;
    }

    private Exam getExamEntityById(Long id) {
        return examRepository.findById(id)
            .filter(e -> !Boolean.TRUE.equals(e.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
    }

    private ExamResponse mapExamToResponse(Exam e) {
        return ExamResponse.builder()
            .id(e.getId()).name(e.getName()).examDate(e.getExamDate())
            .startTime(e.getStartTime()).endTime(e.getEndTime())
            .totalMarks(e.getTotalMarks()).passingMarks(e.getPassingMarks())
            .description(e.getDescription())
            .courseId(e.getCourse() != null ? e.getCourse().getId() : null)
            .courseName(e.getCourse() != null ? e.getCourse().getName() : null)
            .semesterId(e.getSemester() != null ? e.getSemester().getId() : null)
            .semesterName(e.getSemester() != null ? e.getSemester().getName() : null)
            .classroomId(e.getClassroom() != null ? e.getClassroom().getId() : null)
            .classroomNumber(e.getClassroom() != null ? e.getClassroom().getRoomNumber() : null)
            .createdAt(e.getCreatedAt())
            .build();
    }

    private ResultResponse mapResultToResponse(Result r) {
        double percentage = r.getExam().getTotalMarks() > 0
            ? (r.getMarksObtained() / r.getExam().getTotalMarks()) * 100.0 : 0;
        return ResultResponse.builder()
            .id(r.getId())
            .studentId(r.getStudent().getId())
            .studentName(r.getStudent().getFirstName() + " " + r.getStudent().getLastName())
            .rollNumber(r.getStudent().getRollNumber())
            .examId(r.getExam().getId()).examName(r.getExam().getName())
            .courseName(r.getExam().getCourse().getName())
            .marksObtained(r.getMarksObtained())
            .totalMarks(r.getExam().getTotalMarks())
            .grade(r.getGrade()).gpa(r.getGpa()).isPass(r.getIsPass())
            .remarks(r.getRemarks())
            .percentage(Math.round(percentage * 100.0) / 100.0)
            .build();
    }
}
