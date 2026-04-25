package synera.centralis.api.profile.domain.model.valueobjects;

/**
 * Department enum
 * Represents the department where an employee works
 */
public enum Department {
    SALES("Sales"),
    OPERATIONS("Operations"),
    HUMAN_RESOURCES("Human Resources"),
    FINANCE("Finance"),
    ADMINISTRATION("Administration"),
    IT("Information Technology"),
    OTHER("Other");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}