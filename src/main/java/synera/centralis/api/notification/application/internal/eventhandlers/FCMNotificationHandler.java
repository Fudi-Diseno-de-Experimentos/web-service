package synera.centralis.api.notification.application.internal.eventhandlers;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import synera.centralis.api.notification.application.outboundservices.FirebaseCloudMessagingService;
import synera.centralis.api.notification.domain.model.events.NotificationCreatedEvent;
import synera.centralis.api.notification.infrastructure.messaging.fcm.FCMTokenService;

/**
 * Event handler for notification created events.
 * Sends push notifications via Firebase Cloud Messaging when a notification is created.
 *
 * This handler is the bridge that connects notifications saved in the database
 * with FCM push notification delivery.
 */
@Slf4j
@Component
public class FCMNotificationHandler {

    // Dependencies for FCM notification handling
    private final FCMTokenService fcmTokenService;
    private final FirebaseCloudMessagingService firebaseCloudMessagingService;

    public FCMNotificationHandler(
            FCMTokenService fcmTokenService,
            FirebaseCloudMessagingService firebaseCloudMessagingService) {
        this.fcmTokenService = fcmTokenService;
        this.firebaseCloudMessagingService = firebaseCloudMessagingService;
    }

    /**
     * Handles notification created events by sending push notifications via FCM
     *
     * Complete flow:
     * 1. Event → Get FCM Tokens → Send Push Notification
     * 2. Error handling for missing tokens or delivery failures
     * 3. Comprehensive logging for debugging and monitoring
     *
     * Expected flow:
     * 1. Receive NotificationCreatedEvent
     * 2. Get FCM tokens for recipients using fcmTokenService.getTokensForUsers()
     * 3. Send push notification using firebaseCloudMessagingService.sendToTokens()
     * 4. Handle error cases: no tokens, FCM failures, etc.
     *
     * @param event The notification created event containing notification details
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async("fcmTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handle(NotificationCreatedEvent event) {
        log.info("🔔 FCM HANDLER TRIGGERED: Processing notification ID: " + event.getNotificationId());
        log.info("📱 Recipients: " + event.getRecipients().size() + " users");
        log.info("Recipients list: " + event.getRecipients());
        log.info("Title: " + event.getTitle());

        try {
            // 1. Get FCM tokens for the recipients
            List<String> fcmTokens = fcmTokenService.getTokensForUsers(event.getRecipients());

            // 2. Validate that tokens exist
            if (fcmTokens.isEmpty()) {
                log.warn("⚠️ No FCM tokens found for recipients: " + event.getRecipients());
                log.warn("📵 Skipping FCM notification - no devices registered");
                return;
            }

            log.info("📡 Found " + fcmTokens.size() + " FCM tokens for notification delivery");

            // 3. Send push notification via FCM
            boolean success = firebaseCloudMessagingService.sendToTokens(
                fcmTokens,
                event.getTitle(),
                event.getMessage()
            );

            // 4. Handle delivery result
            if (success) {
                log.info("✅ Successfully sent FCM push notification to " + fcmTokens.size() + " devices");
                log.info("🎯 Notification delivered for: " + event.getNotificationId());
            } else {
                log.error("❌ Failed to send FCM push notification for: " + event.getNotificationId());
                log.error("🔥 FCM service returned failure status");
            }

        } catch (IllegalArgumentException e) {
            log.error("🚫 Invalid notification data for " + event.getNotificationId(), e);
        } catch (Exception e) {
            // Handle FCM-specific errors and other unexpected exceptions
            log.error("Error processing FCM push notification for " + event.getNotificationId(), e);

            // Consider implementing retry logic or dead letter queue for failed notifications
            // This could be enhanced with:
            // - Exponential backoff retry mechanism
            // - Dead letter queue for persistently failing notifications
            // - Metrics collection for monitoring delivery rates
        }
    }
}
