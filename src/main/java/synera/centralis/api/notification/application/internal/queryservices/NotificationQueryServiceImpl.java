package synera.centralis.api.notification.application.internal.queryservices;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import synera.centralis.api.notification.domain.model.aggregates.Notification;
import synera.centralis.api.notification.domain.model.queries.GetNotificationByIdQuery;
import synera.centralis.api.notification.domain.model.queries.GetNotificationsByUserIdQuery;
import synera.centralis.api.notification.domain.model.queries.GetNotificationStatusQuery;
import synera.centralis.api.notification.domain.services.NotificationQueryService;
import synera.centralis.api.notification.infrastructure.persistence.jpa.repositories.NotificationRepository;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationQueryServiceImpl implements NotificationQueryService {
    
    private final NotificationRepository notificationRepository;
    
    public NotificationQueryServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Notification> handle(GetNotificationsByUserIdQuery query) {
        return notificationRepository.findByRecipientsContaining(query.userId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> handle(GetNotificationStatusQuery query) {
        return notificationRepository.findById(query.notificationId());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Notification> handle(GetNotificationByIdQuery query) {
        return notificationRepository.findById(query.notificationId());
    }
}