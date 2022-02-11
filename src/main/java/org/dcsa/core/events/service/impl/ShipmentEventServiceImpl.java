package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.ShipmentEvent;
import org.dcsa.core.events.model.UnmappedEvent;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.ShipmentEventRepository;
import org.dcsa.core.events.repository.UnmappedEventRepository;
import org.dcsa.core.events.service.ShipmentEventService;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.BiFunction;


@RequiredArgsConstructor
@Service
public class ShipmentEventServiceImpl extends ExtendedBaseServiceImpl<ShipmentEventRepository, ShipmentEvent, UUID> implements ShipmentEventService {
    private final ShipmentEventRepository shipmentEventRepository;
    private final ReferenceRepository referenceRepository;
    private final UnmappedEventRepository unmappedEventRepository;

    @Override
    public ShipmentEventRepository getRepository() {
        return shipmentEventRepository;
    }

    @Override
    public Mono<ShipmentEvent> findById(UUID id) {
        return shipmentEventRepository.findById(id);
    }

    @Override
    public Mono<ShipmentEvent> loadRelatedEntities(ShipmentEvent shipmentEvent) {
        switch (shipmentEvent.getDocumentTypeCode()) {
            case BKG:
                return shipmentEventReferences
                        .apply(
                                shipmentEvent,
                                referenceRepository.findByCarrierBookingReference(shipmentEvent.getDocumentID()))
                        .thenReturn(shipmentEvent);
            case TRD:
                return shipmentEventReferences
                        .apply(
                                shipmentEvent,
                                referenceRepository.findByTransportDocumentReference(shipmentEvent.getDocumentID()))
                        .thenReturn(shipmentEvent);
            case SHI:
                return shipmentEventReferences
                        .apply(
                                shipmentEvent,
                                referenceRepository.findByShippingInstructionID(shipmentEvent.getDocumentID()))
                        .thenReturn(shipmentEvent);
            default:
                return Mono.just(shipmentEvent);
        }
    }

    private final BiFunction<ShipmentEvent, Flux<Reference>, Mono<ShipmentEvent>>
            shipmentEventReferences =
            (se, rs) ->
                    Mono.justOrEmpty(se)
                            .flatMap(
                                    shipmentEvent ->
                                            rs.collectList()
                                                    .doOnNext(shipmentEvent::setReferences)
                                                    .thenReturn(shipmentEvent));

    @Override
    public Mono<ShipmentEvent> create(ShipmentEvent shipmentEvent) {
        return shipmentEventRepository.save(shipmentEvent).flatMap(
                se -> {
                    UnmappedEvent unmappedEvent = new UnmappedEvent();
                    unmappedEvent.setNewRecord(true);
                    unmappedEvent.setEventID(se.getEventID());
                    unmappedEvent.setEnqueuedAtDateTime(se.getEventCreatedDateTime());
                    return unmappedEventRepository.save(unmappedEvent);
                }).thenReturn(shipmentEvent);
    }

}
