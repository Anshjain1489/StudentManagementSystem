ackage in.springproject.service;

import in.springproject.dto.teacher.*;
import in.springproject.util.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for managing Teacher entities.
 * Provides CRUD operations, search, photo upload and count functionality.
 */
public interface TeacherService {
    TeacherResponse createTeacher(TeacherRequest request);
    TeacherResponse updateTeacher(Long id, TeacherRequest request);
    TeacherResponse getTeacherById(Long id);
    PageResponse<TeacherResponse> getAllTeachers(Pageable pageable);
    PageResponse<TeacherResponse> searchTeachers(String query, Pageable pageable);
    void deleteTeacher(Long id);
    String uploadPhoto(Long id, MultipartFile file);
    long countTeachers();
}
