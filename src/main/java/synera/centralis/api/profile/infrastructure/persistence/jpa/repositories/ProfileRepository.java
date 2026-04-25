package synera.centralis.api.profile.infrastructure.persistence.jpa.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import synera.centralis.api.profile.domain.model.aggregates.Profile;
import synera.centralis.api.profile.domain.model.valueobjects.UserId;

/**
 * Profile repository
 * JPA repository for Profile aggregate
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
    
    /**
     * Find profile by user ID
     * @param userId the user ID
     * @return optional profile
     */
    Optional<Profile> findByUserId(UserId userId);
    
    /**
     * Check if profile exists by user ID
     * @param userId the user ID
     * @return true if exists
     */
    boolean existsByUserId(UserId userId);
}