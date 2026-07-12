package in.springproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "results",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "exam_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Result extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
    @Column(name = "marks_obtained", nullable = false)
    private Double marksObtained;
    @Column(name = "grade")
    private String grade;
    @Column(name = "gpa")
    private Double gpa;
    @Column(name = "is_pass")
    private Boolean isPass;
    @Column(name = "remarks")
    private String remarks;
}
