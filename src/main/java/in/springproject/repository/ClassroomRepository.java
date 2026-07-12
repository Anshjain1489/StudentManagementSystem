package in.springproject.repository;

import in.springproject.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link Classroom} entity operations.
 *
 * <p>Provides lookups by room number, availability flag filtering,
 * and a combined soft-delete and availability query for scheduling.</p>
 */
@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    /**
     * Finds a classroom by its unique room number.
     *
     * @param roomNumber the room number (e.g., "A-101")
     * @return an {@link Optional} containing the classroom if found
     */
    Optional<Classroom> findByRoomNumber(String roomNumber);

    /**
     * Returns all classrooms filtered by their availability status.
     *
     * @param isAvailable {@code true} to find available classrooms,
     *                    {@code false} to find unavailable ones
     * @return list of classrooms matching the availability flag
     */
    List<Classroom> findByIsAvailable(Boolean isAvailable);

    /**
     * Returns all non-deleted classrooms that are currently available.
     * Used for timetable scheduling to present valid classroom options.
     *
     * @return list of non-deleted, available classrooms
     */
    @Query("SELECT c FROM Classroom c WHERE c.deleted = false AND c.isAvailable = true")
    List<Classroom> findAllAvailable();
}
