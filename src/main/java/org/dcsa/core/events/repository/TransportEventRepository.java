package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TransportEventRepository extends ExtendedRepository<TransportEvent, UUID> {

    @Query("SELECT * FROM transport_event a WHERE :eventType IS NULL or a.event_type =:eventType ")
    Flux<TransportEvent> findTransportEventsByFilters(@Param("eventType") EventType eventType);
}
