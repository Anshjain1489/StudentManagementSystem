package in.springproject.controller;

import in.springproject.dto.student.*;
import in.springproject.service.StudentService;
import in.springproject.util.ApiResponse;
import in.springproject.util.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * REST controller for Student management.
 * <p>
 * Base path: {@code /api/v1/students}
 * </p>
 * <p>
 * Role-based access:
 * <ul>
 *   <li>ADMIN – full access (create, update, delete, enroll)</li>
 *   <li>TEACHER – read-only access to student lists</li>
 *   <li>STUDENT – self photo upload</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management APIs")
@SecurityRequirement(name = "bearerAuth")
public class StudentController {

    private final StudentService studentService;

    /**
     * Creates a new student record. Requires ADMIN role.
     *
     * @param request student data payload
     * @return 201 Created with the newly created student
     */
    @PostMapping
    @Operation(summary = "Create a new student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request) {
        StudentResponse response = studentService.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Student created successfully", response, 201));
    }

    /**
     * Returns a paginated list of students. Supports optional search and department filter.
     *
     * @param page         zero-based page index (default 0)
     * @param size         page size (default 10)
     * @param sortBy       field to sort by (default "id")
     * @param sortDir      sort direction: "asc" or "desc" (default "asc")
     * @param search       optional keyword to search by name, email, or roll number
     * @param departmentId optional department filter
     * @return paginated student list
     */
    @GetMapping
    @Operation(summary = "Get all students with pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long departmentId) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<StudentResponse> response;
        if (search != null && !search.isBlank()) {
            response = studentService.searchStudents(search, pageable);
        } else if (departmentId != null) {
            response = studentService.getStudentsByDepartment(departmentId, pageable);
        } else {
            response = studentService.getAllStudents(pageable);
        }

        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", response));
    }

    /**
     * Retrieves a student by their primary key.
     *
     * @param id the student's primary key
     * @return student details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get student by ID")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Student retrieved", studentService.getStudentById(id)));
    }

    /**
     * Retrieves a student by their unique roll number.
     *
     * @param rollNumber the student's roll number
     * @return student details
     */
    @GetMapping("/roll/{rollNumber}")
    @Operation(summary = "Get student by roll number")
    public ResponseEntity<ApiResponse<StudentResponse>> getByRollNumber(@PathVariable String rollNumber) {
        return ResponseEntity.ok(ApiResponse.success("Student retrieved", studentService.getStudentByRollNumber(rollNumber)));
    }

    /**
     * Updates an existing student's information. Requires ADMIN role.
     *
     * @param id      the student's primary key
     * @param request updated student data
     * @return updated student details
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update student details")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", studentService.updateStudent(id, request)));
    }

    /**
     * Soft-deletes a student record. Requires ADMIN role.
     *
     * @param id the student's primary key
     * @return confirmation message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }

    /**
     * Uploads a profile photo for a student.
     *
     * @param id   the student's primary key
     * @param file the image file (multipart)
     * @return publicly accessible photo URL
     */
    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload student photo")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<ApiResponse<String>> uploadPhoto(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String photoUrl = studentService.uploadPhoto(id, file);
        return ResponseEntity.ok(ApiResponse.success("Photo uploaded successfully", photoUrl));
    }

    /**
     * Enrolls a student in the specified course. Requires ADMIN role.
     *
     * @param studentId the student's primary key
     * @param courseId  the course's primary key
     * @return confirmation message
     */
    @PostMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "Enroll student in a course")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> enrollInCourse(
            @PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.enrollInCourse(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Student enrolled in course", null));
    }

    /**
     * Removes a student from the specified course. Requires ADMIN role.
     *
     * @param studentId the student's primary key
     * @param courseId  the course's primary key
     * @return confirmation message
     */
    @DeleteMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "Unenroll student from a course")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> unenrollFromCourse(
            @PathVariable Long studentId, @PathVariable Long courseId) {
        studentService.unenrollFromCourse(studentId, courseId);
        return ResponseEntity.ok(ApiResponse.success("Student unenrolled from course", null));
    }
}
