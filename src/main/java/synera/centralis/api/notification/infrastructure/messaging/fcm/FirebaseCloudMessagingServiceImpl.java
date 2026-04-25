package synera.centralis.api.notification.infrastructure.messaging.fcm;

import org.springframework.stereotype.Service;
import synera.centralis.api.notification.application.outboundservices.FirebaseCloudMessagingService;
import java.util.List;

// @Service
public class FirebaseCloudMessagingServiceImpl implements FirebaseCloudMessagingService {
    @Override
    public boolean sendToToken(String token, String title, String body) { return false; }
    @Override
    public boolean sendToTokens(List<String> tokens, String title, String body) { return false; }
    @Override
    public boolean sendNotification(synera.centralis.api.notification.domain.model.aggregates.Notification notification) { return false; }
}
