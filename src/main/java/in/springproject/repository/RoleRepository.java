package in.springproject.repository;

import in.springproject.entity.Role;
import in.springproject.entity.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for {@link Role} entity operations.
 *
 * <p>Provides lookups by {@link RoleName} enum for role-based access control.</p>
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by its {@link RoleName} enum value.
     *
     * @param name the role name enum constant
     * @return an {@link Optional} containing the role if found
     */
    Optional<Role> findByName(RoleName name);

    /**
     * Checks whether a role with the given name already exists.
     *
     * @param name the role name enum constant
     * @return {@code true} if the role exists
     */
    boolean existsByName(RoleName name);
}
