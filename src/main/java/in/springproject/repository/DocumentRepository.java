package in.springproject.repository;

import in.springproject.entity.Document;
import in.springproject.entity.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for {@link Document} entity operations.
 *
 * <p>Provides lookups for student documents, filtered by document type,
 * and an existence check to enforce one-document-per-type-per-student rules.</p>
 */
@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    /**
     * Returns all documents uploaded for a specific student.
     *
     * @param studentId the student ID
     * @return list of all documents for the student
     */
    List<Document> findByStudentId(Long studentId);

    /**
     * Returns all documents of a specific type for a student.
     *
     * @param studentId the student ID
     * @param type      the document type (e.g., AADHAAR, BIRTH_CERTIFICATE)
     * @return list of documents matching the student and type
     */
    List<Document> findByStudentIdAndType(Long studentId, DocumentType type);

    /**
     * Checks whether a document of the given type already exists for a student.
     * Useful for enforcing unique document constraints before upload.
     *
     * @param studentId the student ID
     * @param type      the document type to check
     * @return {@code true} if a document of this type already exists for the student
     */
    boolean existsByStudentIdAndType(Long studentId, DocumentType type);
}
