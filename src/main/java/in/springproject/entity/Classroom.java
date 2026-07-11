ackage in.springproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classrooms")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Classroom extends BaseEntity {
    @Column(name = "room_number", unique = true, nullable = false)
    private String roomNumber;
    @Column(name = "building")
    private String building;
    @Column(name = "floor")
    private Integer floor;
    @Column(name = "capacity", nullable = false)
    private Integer capacity;
    @Column(name = "has_projector")
    @Builder.Default
    private Boolean hasProjector = false;
    @Column(name = "has_ac")
    @Builder.Default
    private Boolean hasAc = false;
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
}
