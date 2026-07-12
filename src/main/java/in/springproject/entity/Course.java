package in.springproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Course extends BaseEntity {
    @Column(name = "code", unique = true, nullable = false)
    private String code;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    @Column(name = "credits", nullable = false)
    private Integer credits;
    @Column(name = "max_students")
    private Integer maxStudents;
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Student> students = new HashSet<>();
    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Teacher> teachers = new HashSet<>();
}
