package synera.centralis.api.event.domain.model.valueobjects;

/**
 * Invitation response status of an event recipient.
 * <ul>
 *     <li>{@code PENDING} — invited, has not responded yet (default on create / when newly added).</li>
 *     <li>{@code ACCEPTED} — the member accepted the invitation.</li>
 *     <li>{@code DECLINED} — the member declined/cancelled; the event is hidden from their lists.</li>
 * </ul>
 * A {@code null} status (only possible for pre-feature rows before the manual backfill runs)
 * is treated as {@code PENDING} throughout the domain — see {@link EventRecipient#effectiveStatus()}.
 */
public enum RecipientStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}
