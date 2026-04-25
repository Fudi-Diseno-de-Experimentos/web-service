package synera.centralis.api.notification.infrastructure.messaging.fcm;

// import com.google.firebase.messaging.Message;
// import com.google.firebase.messaging.MulticastMessage;
// import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Component;

import java.util.List;

// @Component
public class FCMMessageBuilder {
//
//
//
//    *
//     * Build a single FCM message
//     * @param token FCM token
//     * @param title Notification title
//     * @param message Notification message
//     * @return FCM Message
//     
//    public Message buildMessage(String token, String title, String message) {
//        Notification notification = Notification.builder()
//                .setTitle(title)
//                .setBody(message)
//                .build();
//        
//        return Message.builder()
//                .setToken(token)
//                .setNotification(notification)
//                .build();
//    }
//    
//    *
//     * Build a multicast FCM message for multiple tokens
//     * @param tokens List of FCM tokens
//     * @param title Notification title
//     * @param message Notification message
//     * @return FCM MulticastMessage
//     
//    public MulticastMessage buildMulticastMessage(List<String> tokens, String title, String message) {
//        Notification notification = Notification.builder()
//                .setTitle(title)
//                .setBody(message)
//                .build();
//        
//        return MulticastMessage.builder()
//                .addAllTokens(tokens)
//                .setNotification(notification)
//                .build();
//    }

}
