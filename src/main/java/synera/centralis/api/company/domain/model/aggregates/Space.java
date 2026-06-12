package synera.centralis.api.company.domain.model.aggregates;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import synera.centralis.api.company.domain.model.commands.CreateSpaceCommand;
import synera.centralis.api.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import synera.centralis.api.shared.domain.model.valueobjects.CompanyId;

/**
 * Space aggregate root: a physical room (e.g. "ROOM 1", "ROOM 30") owned and
 * managed by a company's manager. A space stores no availability flag — whether
 * a room is taken on a given day is computed from the events that book it.
 */
@Getter
@Entity
@NoArgsConstructor
@Table(name = "spaces", uniqueConstraints =
        @UniqueConstraint(name = "uk_space_name_company", columnNames = {"name", "company_id"}))
public class Space extends AuditableAbstractAggregateRoot<Space> {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Embedded
    private CompanyId companyId;

    public Space(CreateSpaceCommand command) {
        this.name = validateAndSetName(command.name());
        this.description = validateAndSetDescription(command.description());
        this.companyId = command.companyId();
    }

    /**
     * Updates the editable attributes of the space. A null field is left unchanged.
     */
    public void update(String name, String description) {
        if (name != null) {
            this.name = validateAndSetName(name);
        }
        if (description != null) {
            this.description = validateAndSetDescription(description);
        }
    }

    private String validateAndSetName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Space name cannot be null or empty");
        }
        if (name.length() > 100) {
            throw new IllegalArgumentException("Space name cannot exceed 100 characters");
        }
        return name.trim();
    }

    private String validateAndSetDescription(String description) {
        if (description == null) {
            return null;
        }
        if (description.length() > 500) {
            throw new IllegalArgumentException("Space description cannot exceed 500 characters");
        }
        return description.trim().isEmpty() ? null : description.trim();
    }
}
