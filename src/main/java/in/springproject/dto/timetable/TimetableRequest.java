package in.springproject.dto.timetable;

import in.springproject.entity.enums.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalTime;

/**
 * DTO for creating or updating a timetable entry.
 * Links a course to a specific day, time slot, teacher, classroom and semester.
 */
@Data
public class TimetableRequest {

    @NotNull(message = "Day of week is required")
    private DayOfWeek dayOfWeek;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    private Long teacherId;
    private Long classroomId;
    private Long semesterId;
}
