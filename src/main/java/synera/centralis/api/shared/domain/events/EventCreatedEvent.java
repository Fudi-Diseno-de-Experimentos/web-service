
                package synera.centralis.api.shared.domain.events;

                import java.time.LocalDateTime;
                import java.util.Set;
                import java.util.UUID;

                /**
                 * Domain event fired when a new event is created.
                 * This event triggers notifications to all event recipients.
                 */
                public record EventCreatedEvent(
                        UUID eventId,
                        LocalDateTime occurredAt,
                        UUID businessEventId,
                        String title,
                        String description,
                        LocalDateTime eventDate,
                        UUID createdBy,
                        Set<UUID> recipientIds
                ) implements DomainEvent {

                    /**
                     * Creates a new EventCreatedEvent with current timestamp.
                     */
                    public static EventCreatedEvent create(
                            UUID businessEventId,
                            String title,
                            String description,
                            LocalDateTime eventDate,
                            UUID createdBy,
                            Set<UUID> recipientIds) {
                        return new EventCreatedEvent(
                                UUID.randomUUID(),
                                LocalDateTime.now(),
                                businessEventId,
                                title,
                                description,
                                eventDate,
                                createdBy,
                                recipientIds
                        );
                    }
                }