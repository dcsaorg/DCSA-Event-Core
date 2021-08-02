package org.dcsa.core.events.service.impl;

import jdk.dynalink.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.repository.EventRepository;
import org.dcsa.core.events.repository.PendingEventRepository;
import org.dcsa.core.events.service.*;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Service
@Primary
public class GenericEventServiceImpl extends ExtendedBaseServiceImpl<EventRepository, Event, UUID> implements GenericEventService {

    private final ShipmentEventService shipmentEventService;
    private final TransportEventService transportEventService;
    private final EquipmentEventService equipmentEventService;
    private final OperationsEventService operationsEventService;
    private final EventRepository eventRepository;

    @Override
    public EventRepository getRepository() {
        return eventRepository;
    }

    // TODO: Throw exception, only have implementation in inherited classes
    @Override
    public Flux<Event> findAllExtended(ExtendedRequest<Event> extendedRequest) {
        throw new NotImplementedException();
    }

    @Override
    public Mono<Event> findById(UUID id) {
        throw new NotImplementedException();
//
//        return Mono.<Event>empty()
//                .switchIfEmpty(getTransportEventRelatedEntities(id))
//                .switchIfEmpty(getShipmentEventRelatedEntities(id))
//                .switchIfEmpty(getEquipmentEventRelatedEntities(id))
//                .switchIfEmpty(getOperationsEventRelatedEntities(id))
//                .switchIfEmpty(Mono.error(new NotFoundException("No event was found with id: " + id)));
    }

    public Mono<TransportEvent> getTransportEventRelatedEntities(UUID id) {
        return transportEventService
                .findById(id)
                .flatMap(transportEventService::loadRelatedEntities)
                .doOnNext(applyEventType);
    }

    public Mono<ShipmentEvent> getShipmentEventRelatedEntities(UUID id) {
        return shipmentEventService
                .findById(id)
                .flatMap(shipmentEventService::loadRelatedEntities)
                .doOnNext(applyEventType);
    }

    public Mono<EquipmentEvent> getEquipmentEventRelatedEntities(UUID id) {
        return equipmentEventService
                .findById(id)
                .flatMap(equipmentEventService::loadRelatedEntities)
                .doOnNext(applyEventType);
    }

    public Mono<OperationsEvent> getOperationsEventRelatedEntities(UUID id) {
        return operationsEventService
                .findById(id)
                .flatMap(operationsEventService::loadRelatedEntities)
                .doOnNext(applyEventType);
    }

    private final Consumer<Event> applyEventType =
            (Event event) -> {
                if (event instanceof TransportEvent) {
                    event.setEventType(EventType.TRANSPORT);
                } else if (event instanceof ShipmentEvent) {
                    event.setEventType(EventType.SHIPMENT);
                } else if (event instanceof EquipmentEvent) {
                    event.setEventType(EventType.EQUIPMENT);
                } else if (event instanceof OperationsEvent) {
                    event.setEventType(EventType.OPERATIONS);
                }
            };

    @Override
    public Mono<Event> create(Event event) {
        switch (event.getEventType()) {
            case SHIPMENT:
                return shipmentEventService.create((ShipmentEvent) event).cast(Event.class);
            case TRANSPORT:
                return transportEventService.create((TransportEvent) event).cast(Event.class);
            case EQUIPMENT:
                return equipmentEventService.create((EquipmentEvent) event).cast(Event.class);
            case OPERATIONS:
                return operationsEventService.create((OperationsEvent) event).cast(Event.class);
            default:
                return Mono.error(new IllegalStateException("Unexpected value: " + event.getEventType()));
        }
    }
}
