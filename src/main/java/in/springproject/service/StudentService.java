package in.springproject.service;

import in.springproject.dto.student.*;
import in.springproject.util.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for Student management operations.
 * Covers CRUD, search, photo upload, and course enrollment.
 */
public interface StudentService {

    /**
     * Creates a new student record.
     *
     * @param request student data
     * @return created student details
     */
    StudentResponse createStudent(StudentRequest request);

    /**
     * Updates an existing student's information.
     *
     * @param id      the student's primary key
     * @param request updated student data
     * @return updated student details
     */
    StudentResponse updateStudent(Long id, StudentRequest request);

    /**
     * Retrieves a student by their primary key.
     *
     * @param id the student's primary key
     * @return student details
     */
    StudentResponse getStudentById(Long id);

    /**
     * Retrieves a student by their unique roll number.
     *
     * @param rollNumber the student's roll number
     * @return student details
     */
    StudentResponse getStudentByRollNumber(String rollNumber);

    /**
     * Retrieves a paginated list of all active students.
     *
     * @param pageable pagination and sorting parameters
     * @return paginated student list
     */
    PageResponse<StudentResponse> getAllStudents(Pageable pageable);

    /**
     * Searches students by name, email, or roll number.
     *
     * @param query    the search keyword
     * @param pageable pagination and sorting parameters
     * @return paginated matching student list
     */
    PageResponse<StudentResponse> searchStudents(String query, Pageable pageable);

    /**
     * Retrieves students belonging to a specific department.
     *
     * @param departmentId the department's primary key
     * @param pageable     pagination and sorting parameters
     * @return paginated student list
     */
    PageResponse<StudentResponse> getStudentsByDepartment(Long departmentId, Pageable pageable);

    /**
     * Soft-deletes a student record.
     *
     * @param id the student's primary key
     */
    void deleteStudent(Long id);

    /**
     * Uploads and stores a profile photo for a student.
     *
     * @param id   the student's primary key
     * @param file the photo file
     * @return the publicly accessible URL of the uploaded photo
     */
    String uploadPhoto(Long id, MultipartFile file);

    /**
     * Enrolls a student into a specific course.
     *
     * @param studentId the student's primary key
     * @param courseId  the course's primary key
     */
    void enrollInCourse(Long studentId, Long courseId);

    /**
     * Removes a student from a specific course.
     *
     * @param studentId the student's primary key
     * @param courseId  the course's primary key
     */
    void unenrollFromCourse(Long studentId, Long courseId);

    /**
     * Returns the total count of active (non-deleted) students.
     *
     * @return count of active students
     */
    long countStudents();
}
