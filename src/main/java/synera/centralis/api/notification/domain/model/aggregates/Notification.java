
package synera.centralis.api.notification.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationPriority;
import synera.centralis.api.notification.domain.model.valueobjects.NotificationStatus;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification extends AuditableAbstractAggregateRoot<Notification> {

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, length = 2000)
	private String message;

	@ElementCollection
	@CollectionTable(name = "notification_recipients", joinColumns = @JoinColumn(name = "notification_id"))
	@Column(name = "recipient_id")
	private List<String> recipients; // Stores User UUIDs as strings

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationPriority priority;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private NotificationStatus status;

	public Notification(String title, String message, List<String> recipients, NotificationPriority priority) {
		this.title = title;
		this.message = message;
		this.recipients = recipients;
		this.priority = priority;
		this.status = NotificationStatus.PENDING;
	}

	public void markAsSent() {
		this.status = NotificationStatus.SENT;
	}

	public void markAsFailed() {
		this.status = NotificationStatus.FAILED;
	}

	public void markAsRead() {
		this.status = NotificationStatus.READ;
	}
}
