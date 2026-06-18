package synera.centralis.api.notification.infrastructure.messaging.fcm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import synera.centralis.api.notification.application.outboundservices.FirebaseCloudMessagingService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.BatchResponse;

import java.util.List;

@Slf4j
@Service
public class FirebaseCloudMessagingServiceImpl implements FirebaseCloudMessagingService {

    private final FCMTokenService fcmTokenService;

    public FirebaseCloudMessagingServiceImpl(FCMTokenService fcmTokenService) {
        this.fcmTokenService = fcmTokenService;
    }

    @Override
    public boolean sendToToken(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent message: " + response);
            return true;
        } catch (Exception e) {
            log.error("Error sending FCM message to token", e);
            return false;
        }
    }

    @Override
    public boolean sendToTokens(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) return false;
        try {
            MulticastMessage message = MulticastMessage.builder()
                    .addAllTokens(tokens)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);
            log.info("Successfully sent multicast message. Success count: " + response.getSuccessCount());
            return response.getSuccessCount() > 0;
        } catch (NoSuchMethodError e) {
             // Fallback for older Firebase Admin SDK versions
             log.warn("sendEachForMulticast not found, falling back to loop");
             boolean success = false;
             for (String token : tokens) {
                 if (sendToToken(token, title, body)) success = true;
             }
             return success;
        } catch (Exception e) {
            log.error("Error sending FCM multicast message", e);
            return false;
        }
    }

    @Override
    public boolean sendNotification(synera.centralis.api.notification.domain.model.aggregates.Notification notification) {
        List<String> userIds = notification.getRecipients();
        if (userIds == null || userIds.isEmpty()) {
            return false;
        }
        List<String> tokens = fcmTokenService.getTokensForUsers(userIds);
        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for recipients");
            return false;
        }
        return sendToTokens(tokens, notification.getTitle(), notification.getMessage());
    }
}
