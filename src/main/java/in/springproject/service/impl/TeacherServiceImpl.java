package in.springproject.service.impl;

import in.springproject.dto.teacher.*;
import in.springproject.entity.*;
import in.springproject.exception.*;
import in.springproject.repository.*;
import in.springproject.service.StorageService;
import in.springproject.service.TeacherService;
import in.springproject.util.PageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * Implementation of {@link TeacherService} providing full CRUD operations,
 * search, soft-delete, and photo upload for teachers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final StorageService storageService;

    @Override
    public TeacherResponse createTeacher(TeacherRequest request) {
        if (teacherRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateResourceException("Teacher", "employee ID", request.getEmployeeId());
        }
        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Teacher", "email", request.getEmail());
        }
        Department dept = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        Teacher teacher = Teacher.builder()
            .employeeId(request.getEmployeeId())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .phone(request.getPhone())
            .gender(request.getGender())
            .dateOfBirth(request.getDateOfBirth())
            .qualification(request.getQualification())
            .specialization(request.getSpecialization())
            .experienceYears(request.getExperienceYears())
            .joiningDate(request.getJoiningDate())
            .address(request.getAddress())
            .department(dept)
            .build();

        return mapToResponse(teacherRepository.save(teacher));
    }

    @Override
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {
        Teacher teacher = getTeacherEntityById(id);
        Department dept = departmentRepository.findById(request.getDepartmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        teacher.setFirstName(request.getFirstName());
        teacher.setLastName(request.getLastName());
        teacher.setEmail(request.getEmail());
        teacher.setPhone(request.getPhone());
        teacher.setGender(request.getGender());
        teacher.setQualification(request.getQualification());
        teacher.setSpecialization(request.getSpecialization());
        teacher.setExperienceYears(request.getExperienceYears());
        teacher.setAddress(request.getAddress());
        teacher.setDepartment(dept);

        return mapToResponse(teacherRepository.save(teacher));
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        return mapToResponse(getTeacherEntityById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TeacherResponse> getAllTeachers(Pageable pageable) {
        Page<TeacherResponse> page = teacherRepository.findAllActive(pageable).map(this::mapToResponse);
        return PageResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TeacherResponse> searchTeachers(String query, Pageable pageable) {
        Page<TeacherResponse> page = teacherRepository.searchTeachers(query, pageable).map(this::mapToResponse);
        return PageResponse.from(page);
    }

    @Override
    public void deleteTeacher(Long id) {
        Teacher teacher = getTeacherEntityById(id);
        teacher.setDeleted(true);
        teacherRepository.save(teacher);
    }

    @Override
    public String uploadPhoto(Long id, MultipartFile file) {
        Teacher teacher = getTeacherEntityById(id);
        String url = storageService.storeFile(file, "teachers/photos");
        teacher.setPhotoUrl(url);
        teacherRepository.save(teacher);
        return url;
    }

    @Override
    @Transactional(readOnly = true)
    public long countTeachers() {
        return teacherRepository.countActive();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Teacher getTeacherEntityById(Long id) {
        return teacherRepository.findById(id)
            .filter(t -> !Boolean.TRUE.equals(t.getDeleted()))
            .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
    }

    private TeacherResponse mapToResponse(Teacher t) {
        return TeacherResponse.builder()
            .id(t.getId())
            .employeeId(t.getEmployeeId())
            .firstName(t.getFirstName())
            .lastName(t.getLastName())
            .fullName(t.getFirstName() + " " + t.getLastName())
            .email(t.getEmail())
            .phone(t.getPhone())
            .gender(t.getGender())
            .dateOfBirth(t.getDateOfBirth())
            .qualification(t.getQualification())
            .specialization(t.getSpecialization())
            .experienceYears(t.getExperienceYears())
            .photoUrl(t.getPhotoUrl())
            .joiningDate(t.getJoiningDate())
            .address(t.getAddress())
            .departmentId(t.getDepartment() != null ? t.getDepartment().getId() : null)
            .departmentName(t.getDepartment() != null ? t.getDepartment().getName() : null)
            .createdAt(t.getCreatedAt())
            .build();
    }
}
