package synera.centralis.api.notification.infrastructure.persistence.jpa.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import synera.centralis.api.notification.domain.model.entities.UserFcmToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserFcmTokenRepository extends JpaRepository<UserFcmToken, UUID> {
    
    /**
     * Find active FCM tokens for a user
     * @param userId The user ID
     * @return List of active FCM tokens
     */
    List<UserFcmToken> findByUserIdAndIsActiveTrue(String userId);
    
    /**
     * Find specific FCM token for a user
     * @param userId The user ID
     * @param fcmToken The FCM token
     * @return Optional FCM token entity
     */
    Optional<UserFcmToken> findByUserIdAndFcmToken(String userId, String fcmToken);
    
    /**
     * Find all tokens for a user (active and inactive)
     * @param userId The user ID
     * @return List of all FCM tokens for the user
     */
    List<UserFcmToken> findByUserId(String userId);
    
    /**
     * Deactivate all tokens for a user
     * @param userId The user ID
     */
    @Modifying
    @Query("UPDATE UserFcmToken t SET t.isActive = false WHERE t.userId = :userId")
    void deactivateAllTokensForUser(@Param("userId") String userId);
    
    /**
     * Find token by device ID and user ID
     * @param userId The user ID
     * @param deviceId The device ID
     * @return Optional FCM token entity
     */
    Optional<UserFcmToken> findByUserIdAndDeviceId(String userId, String deviceId);
    
    /**
     * Delete inactive tokens
     */
    @Modifying
    @Query("DELETE FROM UserFcmToken t WHERE t.isActive = false")
    void deleteInactiveTokens();
}