package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.repository.EventRepository;
import org.dcsa.core.events.service.*;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
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

    @Override
    public Flux<Event> findAllExtended(ExtendedRequest<Event> extendedRequest) {
        return super.findAllExtended(extendedRequest);
    }

    @Override
    public Mono<Event> findById(UUID id) {
        throw new NotImplementedException();
    }

    @Override
    public Mono<Event> findByEventTypeAndEventID(EventType eventType, UUID eventID) {
        return eventRepository.findByEventTypeAndEventID(eventType, eventID);
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
        Mono<? extends Event> eventMono;
        EventType eventType = event.getEventType();
        String carrierBookingReference = event.getCarrierBookingReference();
        // Clear these "pseudo-transient" field. They exist on reading from the database, but cannot
        // be included in the create method as the fields do not exist in the table (they are from a view).
        // When they are null, Spring R2DBC will omit them from the INSERT INTO and therefore it
        // happens to work.  We restore the after we have passed them to the database.
        event.setEventType(null);
        event.setCarrierBookingReference(null);
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
        return eventMono.doOnNext(e -> {
            e.setEventType(eventType);
            e.setCarrierBookingReference(carrierBookingReference);
        }).cast(Event.class);
    }
}
