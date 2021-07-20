package org.dcsa.core.events.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("pending_event_queue")
@Data
public class PendingMessage {

    @Id
    @Column("delivery_id")
    private UUID deliveryID;

    @Column("subscription_id")
    private UUID subscriptionID;

    @Column("event_id")
    private UUID eventID;

    @Column("payload")
    private String payload;

    @Column("enqueued_at_date_time")
    private OffsetDateTime enqueuedAtDateTime;

    @Column("last_attempt_date_time")
    private OffsetDateTime lastAttemptDateTime;

    @Column("last_error_message")
    private String lastErrorMessage;

    @Column("retry_count")
    private Integer retryCount = 0;

}
