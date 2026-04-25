package synera.centralis.api.notification.domain.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_fcm_tokens", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "fcm_token"})
})
public class UserFcmToken extends AuditableAbstractAggregateRoot<UserFcmToken> {

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "fcm_token", nullable = false, length = 1000)
    private String fcmToken;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public UserFcmToken(String userId, String fcmToken, String deviceType, String deviceId) {
        this.userId = userId;
        this.fcmToken = fcmToken;
        this.deviceType = deviceType;
        this.deviceId = deviceId;
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void activate() {
        this.isActive = true;
    }
}