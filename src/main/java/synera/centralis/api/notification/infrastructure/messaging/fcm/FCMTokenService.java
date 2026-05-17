package synera.centralis.api.notification.infrastructure.messaging.fcm;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.notification.domain.model.entities.UserFcmToken;
import synera.centralis.api.notification.infrastructure.persistence.jpa.repositories.UserFcmTokenRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FCMTokenService {
    
    private final UserFcmTokenRepository userFcmTokenRepository;
    
    public FCMTokenService(UserFcmTokenRepository userFcmTokenRepository) {
        this.userFcmTokenRepository = userFcmTokenRepository;
    }
    
    /**
     * Get FCM tokens for a list of user IDs
     * @param userIds List of user IDs
     * @return List of FCM tokens
     */
    public List<String> getTokensForUsers(List<String> userIds) {
        return userIds.stream()
                .flatMap(userId -> userFcmTokenRepository.findByUserIdAndIsActiveTrue(userId).stream())
                .map(UserFcmToken::getFcmToken)
                .distinct()
                .toList();
    }
    
    /**
     * Get FCM token for a specific user (returns first active token)
     * @param userId User ID
     * @return FCM token or null if not found
     */
    public String getTokenForUser(String userId) {
        return userFcmTokenRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .findFirst()
                .map(UserFcmToken::getFcmToken)
                .orElse(null);
    }
    
    /**
     * Get all active FCM tokens for a user
     * @param userId User ID
     * @return List of active FCM tokens
     */
    public List<UserFcmToken> getActiveTokensForUser(String userId) {
        return userFcmTokenRepository.findByUserIdAndIsActiveTrue(userId);
    }
    
    /**
     * Save or update FCM token for a user
     * @param userId User ID
     * @param fcmToken FCM token
     * @param deviceType Device type (iOS, Android)
     * @param deviceId Device identifier
     * @return Saved FCM token entity
     */
    @Transactional
    public UserFcmToken saveTokenForUser(String userId, String fcmToken, String deviceType, String deviceId) {
        // Check if token already exists
        Optional<UserFcmToken> existingToken = userFcmTokenRepository.findByUserIdAndFcmToken(userId, fcmToken);
        
        if (existingToken.isPresent()) {
            var token = existingToken.get();
            token.activate(); // Ensure it's active
            token.setDeviceType(deviceType);
            token.setDeviceId(deviceId);
            return userFcmTokenRepository.save(token);
        }
        
        // Check if there's a token for this device
        Optional<UserFcmToken> deviceToken = userFcmTokenRepository.findByUserIdAndDeviceId(userId, deviceId);
        if (deviceToken.isPresent()) {
            var token = deviceToken.get();
            token.setFcmToken(fcmToken);
            token.setDeviceType(deviceType);
            token.activate();
            return userFcmTokenRepository.save(token);
        }
        
        // Create new token
        var newToken = new UserFcmToken(userId, fcmToken, deviceType, deviceId);
        return userFcmTokenRepository.save(newToken);
    }
    
    /**
     * Remove FCM token for a user
     * @param userId User ID
     * @param fcmToken FCM token to remove
     * @return true if removed, false if not found
     */
    @Transactional
    public boolean removeTokenForUser(String userId, String fcmToken) {
        Optional<UserFcmToken> token = userFcmTokenRepository.findByUserIdAndFcmToken(userId, fcmToken);
        if (token.isPresent()) {
            token.get().deactivate();
            userFcmTokenRepository.save(token.get());
            return true;
        }
        return false;
    }
    
    /**
     * Remove all FCM tokens for a user (logout)
     * @param userId User ID
     */
    @Transactional
    public void removeAllTokensForUser(String userId) {
        userFcmTokenRepository.deactivateAllTokensForUser(userId);
    }
    
    /**
     * Clean up inactive tokens (maintenance task)
     */
    @Transactional
    public void cleanupInactiveTokens() {
        userFcmTokenRepository.deleteInactiveTokens();
    }
}