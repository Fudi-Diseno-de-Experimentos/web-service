package synera.centralis.api.iam.infrastructure.persistence.jpa.repositories;

import java.util.Optional;
import java.util.UUID;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import synera.centralis.api.iam.domain.model.aggregates.User;

/**
 * This interface is responsible for providing the User entity related operations.
 * It extends the JpaRepository interface.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID>
{
    List<User> findAllByCompanyId(CompanyId companyId);

    Optional<User> findByIdAndCompanyId(UUID id, CompanyId companyId);

    /**
     * This method is responsible for finding the user by username.
     * @param username The username.
     * @return The user object.
     */
    Optional<User> findByUsername(String username);

    /**
     * This method is responsible for checking if the user exists by username.
     * @param username The username.
     * @return True if the user exists, false otherwise.
     */
    boolean existsByUsername(String username);

}
