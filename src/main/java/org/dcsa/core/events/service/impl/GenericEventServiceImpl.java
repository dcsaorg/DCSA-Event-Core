package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.repository.EventRepository;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.EquipmentEventService;
import org.dcsa.core.events.service.GenericEventService;
import org.dcsa.core.events.service.ShipmentEventService;
import org.dcsa.core.events.service.TransportEventService;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class GenericEventServiceImpl extends ExtendedBaseServiceImpl<EventRepository, Event, UUID> implements GenericEventService {

    @Autowired
    private ShipmentEventService shipmentEventService;

    @Autowired
    private TransportEventService transportEventService;

    @Autowired
    private EquipmentEventService equipmentEventService;

    @Autowired
    private EventRepository eventRepository;

    @Override
    public EventRepository getRepository() {
        return eventRepository;
    }

    @Override
    public Flux<Event> findAllExtended(ExtendedRequest<Event> extendedRequest) {
        Flux<Event> events = super.findAllExtended(extendedRequest);

        Flux<TransportEvent> transportEvents = events
                .filter(event -> event.getEventType() == EventType.TRANSPORT)
                .map(event -> (TransportEvent) event)
                .flatMap(transportEventService::mapReferences);

        Flux<ShipmentEvent> shipmentEvents = events
                .filter(event -> event.getEventType() == EventType.SHIPMENT)
                .map(event -> (ShipmentEvent) event);

        Flux<EquipmentEvent> equipmentEvents = events
                .filter(event -> event.getEventType() == EventType.EQUIPMENT)
                .map(event -> (EquipmentEvent) event);

        return Flux.merge(transportEvents, shipmentEvents, equipmentEvents);
    }

    @Override
    public Mono<Event> findById(UUID id) {
        return Mono.<Event>empty()
                .switchIfEmpty(transportEventService.findById(id).cast(Event.class))
                .switchIfEmpty(shipmentEventService.findById(id).cast(Event.class))
                .switchIfEmpty(equipmentEventService.findById(id).cast(Event.class))
                .switchIfEmpty(Mono.error(new NotFoundException("No event was found with id: " + id)));
    }

    @Override
    public Mono<Event> create(Event event) {
        switch (event.getEventType()) {
            case SHIPMENT:
                return shipmentEventService.create((ShipmentEvent) event).cast(Event.class);
            case TRANSPORT:
                return transportEventService.create((TransportEvent) event).cast(Event.class);
            case EQUIPMENT:
                return equipmentEventService.create((EquipmentEvent) event).cast(Event.class);
            default:
                return Mono.error(new IllegalStateException("Unexpected value: " + event.getEventType()));
        }
    }
}
