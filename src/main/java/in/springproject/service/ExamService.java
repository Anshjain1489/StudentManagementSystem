package in.springproject.service;

import in.springproject.dto.exam.*;
import in.springproject.util.PageResponse;
import org.springframework.data.domain.Pageable;
import java.util.List;

/**
 * Service interface for exam and result management.
 * Handles exam CRUD, marks entry, grading, and GPA/CGPA calculation.
 */
public interface ExamService {
    ExamResponse createExam(ExamRequest request);
    ExamResponse updateExam(Long id, ExamRequest request);
    ExamResponse getExamById(Long id);
    PageResponse<ExamResponse> getAllExams(Pageable pageable);
    List<ExamResponse> getExamsByCourse(Long courseId);
    List<ExamResponse> getExamsBySemester(Long semesterId);
    void deleteExam(Long id);

    ResultResponse enterResult(ResultRequest request);
    ResultResponse updateResult(Long id, ResultRequest request);
    List<ResultResponse> getResultsByExam(Long examId);
    List<ResultResponse> getResultsByStudent(Long studentId);
    double calculateCgpa(Long studentId);
}
