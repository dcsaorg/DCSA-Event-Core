package org.dcsa.core.events.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.events.model.PendingMessage;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PendingEventRepository extends ExtendedRepository<PendingMessage, UUID> {

    // PostgreSQL specific (due to "FOR UPDATE SKIP LOCKED")
    @Query("DELETE FROM unmapped_event_queue WHERE event_id = ("
            + "  SELECT event_id FROM unmapped_event_queue FOR UPDATE SKIP LOCKED LIMIT 1"
            + ") RETURNING event_id")
    Mono<UUID> pollUnmappedEventID();

    @Modifying
    @Query("INSERT INTO unmapped_event_queue (event_id) VALUES (:eventID)")
    Mono<Void> enqueueUnmappedEventID(UUID eventID);

    // PostgreSQL specific (due to "FOR UPDATE SKIP LOCKED")
    @Query("DELETE FROM pending_event_queue WHERE delivery_id = ("
            + "  SELECT delivery_id FROM pending_event_queue"
            + "  JOIN event_subscription ON (pending_event_queue.subscription_id = event_subscription.subscription_id)"
            + "  WHERE retry_after IS NULL OR retry_after <= now()"
            + "  FOR UPDATE SKIP LOCKED LIMIT 1"
            + ") RETURNING *")
    Mono<PendingMessage> pollPendingEvent();
}
