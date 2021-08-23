package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.ShipmentEvent;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.ShipmentEventRepository;
import org.dcsa.core.events.service.ShipmentEventService;
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

    @Override
    public ShipmentEventRepository getRepository() {
        return shipmentEventRepository;
    }

    //Overriding base method here, as it marks empty results as an error, meaning we can't use switchOnEmpty()
    @Override
    public Mono<ShipmentEvent> findById(UUID id) {
        return getRepository().findById(id);
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

    public Mono<ShipmentEvent> insert(ShipmentEvent shipmentEvent) {
        return preCreateHook(shipmentEvent)
                .flatMap(this::preSaveHook)
                .flatMap(shipmentEventRepository::insert);
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

}
