package synera.centralis.api.notification.domain.model.queries;

public record GetNotificationsByUserIdQuery(
        String userId
) {
    public GetNotificationsByUserIdQuery {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
    }
}