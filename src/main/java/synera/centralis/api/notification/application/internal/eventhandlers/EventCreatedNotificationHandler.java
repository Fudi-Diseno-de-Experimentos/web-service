package synera.centralis.api.notification.application.internal.eventhandlers;

 import java.util.List;
 import java.util.Set;
 import java.util.UUID;
 import java.util.logging.Logger;

 import org.springframework.scheduling.annotation.Async;
 import org.springframework.stereotype.Component;
 import org.springframework.transaction.annotation.Propagation;
 import org.springframework.transaction.annotation.Transactional;
 import org.springframework.transaction.event.TransactionPhase;
 import org.springframework.transaction.event.TransactionalEventListener;

 import synera.centralis.api.notification.domain.model.commands.CreateNotificationCommand;
 import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;
 import synera.centralis.api.notification.domain.services.NotificationCommandService;
 import synera.centralis.api.shared.domain.events.EventCreatedEvent;

 @Component
 public class EventCreatedNotificationHandler {

     private static final Logger logger = Logger.getLogger(EventCreatedNotificationHandler.class.getName());

     private final NotificationCommandService notificationCommandService;

     public EventCreatedNotificationHandler(NotificationCommandService notificationCommandService) {
         this.notificationCommandService = notificationCommandService;
     }

     @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
     @Async("eventTaskExecutor")
     @Transactional(propagation = Propagation.REQUIRES_NEW)
     public void handle(EventCreatedEvent event) {
         logger.info("📅 Processing event creation notification for: " + event.title());

         try {

             Set<UUID> recipients = event.recipientIds();
             logger.info("📥 Recipients raw from event: " + (recipients == null ? "null" : recipients.toString()));

             if (recipients != null && !recipients.isEmpty()) {
                 String creatorIdStr = event.createdBy().toString();

                 // Convertir a strings y eliminar explícitamente al creador
                 var recipientIds = recipients.stream()
                         .map(UUID::toString)
                         .filter(idStr -> !idStr.equals(creatorIdStr))
                         .toList();

                 logger.info("📧 RecipientIds after excluding creator: " + recipientIds);

                 if (!recipientIds.isEmpty()) {
                     var attendeeCommand = new CreateNotificationCommand(
                             "Has sido añadido a un evento",
                             "Has sido añadido al evento: " + event.title(),
                             recipientIds,
                             NotificationPriority.MEDIUM
                     );
                     var result = notificationCommandService.handle(attendeeCommand);
                     if (result != null && result.isPresent()) {
                         logger.info("✅ Notification sent to attendees: " + recipientIds.size());
                     } else {
                         logger.severe("❌ Failed to create notifications for attendees (service returned empty)");
                     }
                 } else {
                     logger.info("ℹ️ No attendees to notify (after excluding creator)");
                 }
             } else {
                 logger.info("ℹ️ No recipientIds present in EventCreatedEvent");
             }

         } catch (Exception e) {
             logger.severe("❌ Error processing event notification: " + e.getMessage());
             e.printStackTrace();
         }
     }
 }