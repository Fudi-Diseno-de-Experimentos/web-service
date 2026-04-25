package synera.centralis.api.shared.interfaces.rest.resources;

import java.time.LocalDateTime;

public record ErrorResource(
        String message,
        String error,
        int status,
        LocalDateTime timestamp
) {
    public ErrorResource(String message, String error, int status) {
        this(message, error, status, LocalDateTime.now());
    }
}
