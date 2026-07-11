package in.springproject.repository;

import in.springproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for {@link User} entity operations.
 *
 * <p>Provides standard CRUD operations via {@link JpaRepository} along with
 * custom finders for username, email, token-based lookups, and soft-delete support.</p>
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique username.
     *
     * @param username the username to search
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address to search
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds a user by their password-reset token.
     *
     * @param token the reset token
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByResetPasswordToken(String token);

    /**
     * Finds a user by their refresh token.
     *
     * @param refreshToken the JWT refresh token
     * @return an {@link Optional} containing the user if found
     */
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * Checks whether a user with the given username already exists.
     *
     * @param username the username to check
     * @return {@code true} if a user with this username exists
     */
    boolean existsByUsername(String username);

    /**
     * Checks whether a user with the given email already exists.
     *
     * @param email the email to check
     * @return {@code true} if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Soft-deletes a user by setting {@code deleted = true}.
     *
     * @param id the ID of the user to soft-delete
     */
    @Modifying
    @Query("UPDATE User u SET u.deleted = true WHERE u.id = :id")
    void softDeleteById(Long id);

    /**
     * Returns all users that have not been soft-deleted.
     *
     * @return list of active users
     */
    @Query("SELECT u FROM User u WHERE u.deleted = false")
    List<User> findAllActive();
}
