package synera.centralis.api.dashboard.domain.model.valueobjects;

/**
 * Content Type enumeration for distinguishing between announcements and events
 */
public enum ContentType {
    ANNOUNCEMENT("ANNOUNCEMENT"),
    EVENT("EVENT");

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ContentType fromString(String text) {
        for (ContentType type : ContentType.values()) {
            if (type.value.equalsIgnoreCase(text)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No constant with text " + text + " found");
    }
}