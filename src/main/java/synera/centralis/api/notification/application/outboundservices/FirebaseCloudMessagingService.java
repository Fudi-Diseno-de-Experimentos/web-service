package synera.centralis.api.notification.application.outboundservices;

import synera.centralis.api.notification.domain.model.aggregates.Notification;

import java.util.List;

public interface FirebaseCloudMessagingService {
    
    /**
     * Send notification to Firebase Cloud Messaging
     * @param notification The notification to send
     * @return true if sent successfully, false otherwise
     */
    boolean sendNotification(Notification notification);
    
    /**
     * Send notification to specific FCM tokens
     * @param tokens List of FCM tokens
     * @param title Notification title
     * @param message Notification message
     * @return true if sent successfully, false otherwise
     */
    boolean sendToTokens(List<String> tokens, String title, String message);
    
    /**
     * Send notification to a single FCM token
     * @param token FCM token
     * @param title Notification title
     * @param message Notification message
     * @return true if sent successfully, false otherwise
     */
    boolean sendToToken(String token, String title, String message);
}