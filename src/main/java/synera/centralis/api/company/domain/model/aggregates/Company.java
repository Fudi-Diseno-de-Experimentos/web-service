package synera.centralis.api.company.domain.model.aggregates;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import synera.centralis.api.company.domain.model.commands.CreateCompanyCommand;

import synera.centralis.api.company.domain.model.valueobjects.UserId;
import jakarta.persistence.Embedded;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;

@Entity
@Getter
@Setter
public class Company extends AuditableAbstractAggregateRoot<Company> {

    private String ruc;
    private String nombre;
    private String iconUrl;
    private boolean isActive;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "userId", column = @Column(name = "user_id"))
    })
    private UserId userId;

    public Company() {}

    public Company(CreateCompanyCommand command) {
        this.ruc = command.ruc();
        this.nombre = command.nombre();
        this.iconUrl = command.iconUrl();
        this.isActive = command.isActive();
        this.userId = command.userId() != null ? new UserId(command.userId()) : null;
    }

    public Company update(String ruc, String nombre, String iconUrl, boolean isActive) {
        this.ruc = ruc;
        this.nombre = nombre;
        this.iconUrl = iconUrl;
        this.isActive = isActive;
        return this;
    }
}
