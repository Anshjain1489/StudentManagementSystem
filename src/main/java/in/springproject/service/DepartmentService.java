ackage in.springproject.service;

import in.springproject.dto.department.*;

import java.util.List;

/**
 * Service interface for Department management operations.
 * Provides CRUD functionality and a count utility for dashboard metrics.
 */
public interface DepartmentService {

    /**
     * Creates a new department.
     *
     * @param request department data including optional head teacher
     * @return created department details
     */
    DepartmentResponse createDepartment(DepartmentRequest request);

    /**
     * Updates an existing department's information.
     *
     * @param id      the department's primary key
     * @param request updated department data
     * @return updated department details
     */
    DepartmentResponse updateDepartment(Long id, DepartmentRequest request);

    /**
     * Retrieves a department by its primary key.
     *
     * @param id the department's primary key
     * @return department details
     */
    DepartmentResponse getDepartmentById(Long id);

    /**
     * Returns all active (non-deleted) departments.
     *
     * @return list of department details
     */
    List<DepartmentResponse> getAllDepartments();

    /**
     * Soft-deletes a department record.
     *
     * @param id the department's primary key
     */
    void deleteDepartment(Long id);

    /**
     * Returns the total count of active departments.
     *
     * @return count of active departments
     */
    long countDepartments();
}
