package in.springproject.controller;

import in.springproject.dto.exam.*;
import in.springproject.service.ExamService;
import in.springproject.util.ApiResponse;
import in.springproject.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for exam and result management.
 * Endpoint: /api/v1/exams
 */
@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
@Tag(name = "Exams", description = "Exam and result management APIs")
@SecurityRequirement(name = "bearerAuth")
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Create a new exam")
    public ResponseEntity<ApiResponse<ExamResponse>> create(@Valid @RequestBody ExamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Exam created", examService.createExam(request), 201));
    }

    @GetMapping
    @Operation(summary = "Get all exams (paginated)")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved",
            examService.getAllExams(PageRequest.of(page, size, Sort.by("examDate").descending()))));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exam by ID")
    public ResponseEntity<ApiResponse<ExamResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Exam retrieved", examService.getExamById(id)));
    }

    @GetMapping("/course/{courseId}")
    @Operation(summary = "Get exams by course")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved", examService.getExamsByCourse(courseId)));
    }

    @GetMapping("/semester/{semesterId}")
    @Operation(summary = "Get exams by semester")
    public ResponseEntity<ApiResponse<List<ExamResponse>>> getBySemester(@PathVariable Long semesterId) {
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved", examService.getExamsBySemester(semesterId)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update exam details")
    public ResponseEntity<ApiResponse<ExamResponse>> update(
            @PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Exam updated", examService.updateExam(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete exam")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.success("Exam deleted", null));
    }

    // ─── Result Endpoints ─────────────────────────────────────────────────────

    @PostMapping("/results")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Enter exam result for a student")
    public ResponseEntity<ApiResponse<ResultResponse>> enterResult(@Valid @RequestBody ResultRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Result entered", examService.enterResult(request), 201));
    }

    @PutMapping("/results/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Update an existing result")
    public ResponseEntity<ApiResponse<ResultResponse>> updateResult(
            @PathVariable Long id, @Valid @RequestBody ResultRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Result updated", examService.updateResult(id, request)));
    }

    @GetMapping("/{examId}/results")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get all results for an exam")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> getResultsByExam(@PathVariable Long examId) {
        return ResponseEntity.ok(ApiResponse.success("Results retrieved", examService.getResultsByExam(examId)));
    }

    @GetMapping("/students/{studentId}/results")
    @Operation(summary = "Get all exam results for a student")
    public ResponseEntity<ApiResponse<List<ResultResponse>>> getResultsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("Results retrieved", examService.getResultsByStudent(studentId)));
    }

    @GetMapping("/students/{studentId}/cgpa")
    @Operation(summary = "Calculate CGPA for a student")
    public ResponseEntity<ApiResponse<Double>> getCgpa(@PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success("CGPA calculated", examService.calculateCgpa(studentId)));
    }
}
