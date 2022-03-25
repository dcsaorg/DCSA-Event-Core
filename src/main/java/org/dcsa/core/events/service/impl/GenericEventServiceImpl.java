package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.repository.EventRepository;
import org.dcsa.core.events.repository.PendingEventRepository;
import org.dcsa.core.events.service.*;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class GenericEventServiceImpl extends org.dcsa.core.service.impl.QueryServiceImpl<EventRepository, Event, UUID> implements GenericEventService, org.dcsa.core.service.AsymmetricQueryService<Event, Event, UUID> {

    private final Set<EventType> ALL_EVENT_TYPES = Set.copyOf(EnumSet.allOf(EventType.class));

    protected final ShipmentEventService shipmentEventService;
    protected final TransportEventService transportEventService;
    protected final EquipmentEventService equipmentEventService;
    protected final OperationsEventService operationsEventService;
    protected final EventRepository eventRepository;
    protected final PendingEventRepository pendingEventRepository;

    @Override
    public EventRepository getRepository() {
        return eventRepository;
    }

    @Override
    public Flux<Event> findAllExtended(ExtendedRequest<Event> extendedRequest) {
        Set<EventType> eventTypes = getSupportedEvents();
        return super.findAllExtended(extendedRequest)
                // TODO: Push this filter into the extendedRequest, so the database does not spend time on them.
                .filter(e -> eventTypes.contains(e.getEventType()))
                .concatMap(event -> {
                    switch (event.getEventType()) {
                        case TRANSPORT:
                            return transportEventService.loadRelatedEntities((TransportEvent) event);
                        case EQUIPMENT:
                            return equipmentEventService.loadRelatedEntities((EquipmentEvent) event);
                        case SHIPMENT:
                            return shipmentEventService.loadRelatedEntities((ShipmentEvent) event);
                        case OPERATIONS:
                            return operationsEventService.loadRelatedEntities((OperationsEvent) event);
                        default:
                            throw new IllegalStateException("Unsupported event type: " + event.getClass());
                    }
                });
    }

    @Override
    public Mono<Event> findById(UUID id) {
        return Mono.<Event>empty()
                .switchIfEmpty(getTransportEventRelatedEntities(id))
                .switchIfEmpty(getShipmentEventRelatedEntities(id))
                .switchIfEmpty(getEquipmentEventRelatedEntities(id))
                .switchIfEmpty(getOperationsEventRelatedEntities(id))
                .switchIfEmpty(Mono.error(new NotFoundException("No event was found with id: " + id)));
    }

    protected Set<EventType> getSupportedEvents() {
        return ALL_EVENT_TYPES;
    }

    @Override
    public Mono<Event> findByEventTypeAndEventID(EventType eventType, UUID eventID) {
        return eventRepository.findByEventTypeAndEventID(eventType, eventID);
    }

    protected Mono<TransportEvent> getTransportEventRelatedEntities(UUID id) {
        if (!getSupportedEvents().contains(EventType.TRANSPORT)) {
            return Mono.empty();
        }
        return transportEventService
                .findById(id)
                .flatMap(transportEventService::loadRelatedEntities)
                .doOnNext(applyEventType);
    }

    protected Mono<ShipmentEvent> getShipmentEventRelatedEntities(UUID id) {
        if (!getSupportedEvents().contains(EventType.SHIPMENT)) {
            return Mono.empty();
        }
        return shipmentEventService
                .findById(id)
                .flatMap(shipmentEventService::loadRelatedEntities)
                .doOnNext(applyEventType);
    }

    protected Mono<EquipmentEvent> getEquipmentEventRelatedEntities(UUID id) {
        if (!getSupportedEvents().contains(EventType.EQUIPMENT)) {
            return Mono.empty();
        }
        return equipmentEventService
                .findById(id)
                .flatMap(equipmentEventService::loadRelatedEntities)
                .doOnNext(applyEventType);
    }

    protected Mono<OperationsEvent> getOperationsEventRelatedEntities(UUID id) {
        if (!getSupportedEvents().contains(EventType.OPERATIONS)) {
            return Mono.empty();
        }
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
                } else {
                    throw new IllegalStateException("Unsupported event type: " + event.getClass());
                }
            };

    @Override
    public Mono<Event> create(Event event) {
        Mono<? extends Event> eventMono;
        EventType eventType = event.getEventType();
        if (!getSupportedEvents().contains(event.getEventType())) {
            throw new IllegalArgumentException("Unsupported event type: " + event.getEventType());
        }
        // Clear these "pseudo-transient" field. They exist on reading from the database, but cannot
        // be included in the create method as the fields do not exist in the table (they are from a view).
        // When they are null, Spring R2DBC will omit them from the INSERT INTO and therefore it
        // happens to work.  We restore the after we have passed them to the database.
        event.setEventType(null);
        switch (eventType) {
            case SHIPMENT:
                eventMono = shipmentEventService.create((ShipmentEvent) event);
                break;
            case TRANSPORT:
                eventMono = transportEventService.create((TransportEvent) event);
                break;
            case EQUIPMENT:
                eventMono = equipmentEventService.create((EquipmentEvent) event);
                break;
            case OPERATIONS:
                eventMono = operationsEventService.create((OperationsEvent) event);
                break;
            default:
                return Mono.error(new IllegalStateException("Unexpected value: " + event.getEventType()));
        }
        return eventMono.doOnNext(e -> e.setEventType(eventType))
          .cast(Event.class);
    }
}
