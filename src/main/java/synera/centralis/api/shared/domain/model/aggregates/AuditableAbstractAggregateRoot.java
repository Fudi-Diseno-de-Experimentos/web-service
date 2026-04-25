package synera.centralis.api.shared.domain.model.aggregates;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
import java.util.UUID;

/**
 * Abstract class for aggregate roots that need auditing capabilities.
 * It extends AbstractAggregateRoot to support domain events.
 *
 * @param <T> el tipo concreto del agregado
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@SuppressWarnings("unchecked")
public abstract class AuditableAbstractAggregateRoot<T extends AbstractAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    /**
     * Registra un evento de dominio.
     */
    public void addDomainEvent(Object event) {
        registerEvent(event);
    }

    /**
     * Devuelve la instancia concreta del agregado.
     * El cast es seguro porque T es el tipo de la subclase.
     */
    protected T self() {
        return (T) this;
    }
}
