ackage in.springproject.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "semesters")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Semester extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = false;
    @Column(name = "academic_year", nullable = false)
    private String academicYear;
    @OneToMany(mappedBy = "semester", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Exam> exams = new ArrayList<>();
}
