package in.springproject.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.springproject.dto.student.StudentRequest;
import in.springproject.dto.student.StudentResponse;
import in.springproject.entity.enums.Gender;
import in.springproject.exception.ResourceNotFoundException;
import in.springproject.service.StudentService;
import in.springproject.util.PageResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayName("StudentController Integration Tests")
class StudentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private StudentService studentService;

    private StudentResponse buildStudentResponse() {
        return StudentResponse.builder()
            .id(1L).rollNumber("CS2024001")
            .firstName("John").lastName("Doe")
            .fullName("John Doe")
            .email("john.doe@student.edu")
            .gender(Gender.MALE)
            .departmentName("Computer Science")
            .build();
    }

    @Test
    @DisplayName("GET /api/v1/students - Should return paginated students")
    @WithMockUser(roles = "ADMIN")
    void shouldReturnPaginatedStudents() throws Exception {
        PageResponse<StudentResponse> pageResponse = PageResponse.<StudentResponse>builder()
            .content(List.of(buildStudentResponse()))
            .totalElements(1).totalPages(1).pageNumber(0).pageSize(10)
            .first(true).last(true).build();

        when(studentService.getAllStudents(any(Pageable.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/students"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content[0].rollNumber").value("CS2024001"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return student by ID")
    @WithMockUser(roles = "STUDENT")
    void shouldReturnStudentById() throws Exception {
        when(studentService.getStudentById(1L)).thenReturn(buildStudentResponse());

        mockMvc.perform(get("/api/v1/students/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.fullName").value("John Doe"));
    }

    @Test
    @DisplayName("GET /api/v1/students/{id} - Should return 404 when student not found")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn404WhenStudentNotFound() throws Exception {
        when(studentService.getStudentById(999L))
            .thenThrow(new ResourceNotFoundException("Student", "id", 999L));

        mockMvc.perform(get("/api/v1/students/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Should return 403 for STUDENT role")
    @WithMockUser(roles = "STUDENT")
    void shouldReturn403ForStudentRoleOnDelete() throws Exception {
        mockMvc.perform(delete("/api/v1/students/1").with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /api/v1/students/{id} - Should delete student for ADMIN role")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteStudentForAdminRole() throws Exception {
        doNothing().when(studentService).deleteStudent(1L);

        mockMvc.perform(delete("/api/v1/students/1").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }
}
