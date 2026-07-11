ackage in.springproject.controller;

import in.springproject.dto.department.*;
import in.springproject.service.DepartmentService;
import in.springproject.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Department management.
 * <p>
 * Base path: {@code /api/v1/departments}
 * </p>
 * <p>
 * Role-based access:
 * <ul>
 *   <li>ADMIN – full access (create, update, delete)</li>
 *   <li>All authenticated users – read access</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department management APIs")
@SecurityRequirement(name = "bearerAuth")
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * Creates a new department. Requires ADMIN role.
     *
     * @param request department data payload
     * @return 201 Created with the newly created department
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> create(@Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Department created", departmentService.createDepartment(request), 201));
    }

    /**
     * Returns all active departments.
     *
     * @return list of department details
     */
    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success("Departments retrieved", departmentService.getAllDepartments()));
    }

    /**
     * Retrieves a department by its primary key.
     *
     * @param id the department's primary key
     * @return department details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<DepartmentResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Department retrieved", departmentService.getDepartmentById(id)));
    }

    /**
     * Updates an existing department. Requires ADMIN role.
     *
     * @param id      the department's primary key
     * @param request updated department data
     * @return updated department details
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update department")
    public ResponseEntity<ApiResponse<DepartmentResponse>> update(
            @PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Department updated", departmentService.updateDepartment(id, request)));
    }

    /**
     * Soft-deletes a department. Requires ADMIN role.
     *
     * @param id the department's primary key
     * @return confirmation message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete department")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department deleted", null));
    }
}
