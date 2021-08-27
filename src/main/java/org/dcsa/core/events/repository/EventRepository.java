package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventRepository extends ExtendedRepository<Event, UUID> {

    Mono<Event> findByEventTypeAndEventID(EventType eventType, UUID eventID);
}
