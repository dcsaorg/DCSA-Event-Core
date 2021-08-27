package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.enums.EventType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface GenericEventService extends EventService<Event> {

    Mono<Event> findByEventTypeAndEventID(EventType eventType, UUID eventID);
}
