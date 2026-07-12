package in.springproject.repository;

import in.springproject.entity.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Parent} entity operations.
 *
 * <p>Provides lookup of parent records associated with a specific student.
 * A student may have multiple parent/guardian records (e.g., father, mother).</p>
 */
@Repository
public interface ParentRepository extends JpaRepository<Parent, Long> {

    /**
     * Returns all parent/guardian records associated with a specific student.
     *
     * @param studentId the student ID
     * @return list of parent records for the student
     */
    List<Parent> findByStudentId(Long studentId);
}
