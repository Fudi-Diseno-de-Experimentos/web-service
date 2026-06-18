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

    @Column(name = "join_code", unique = true, length = 6)
    private String joinCode;

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
        this.joinCode = generateJoinCode();
    }

    private String generateJoinCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new java.lang.StringBuilder(6);
        java.util.Random rnd = new java.util.Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Assigns a freshly generated join code. Used by the command service to
     * resolve the rare collision against the {@code unique} constraint before
     * persisting, instead of letting the insert blow up with a raw 500.
     */
    public void regenerateJoinCode() {
        this.joinCode = generateJoinCode();
    }

    public Company update(String ruc, String nombre, String iconUrl, boolean isActive) {
        this.ruc = ruc;
        this.nombre = nombre;
        this.iconUrl = iconUrl;
        this.isActive = isActive;
        return this;
    }
}
