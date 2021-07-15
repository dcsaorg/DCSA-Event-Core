package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.repository.EquipmentEventRepository;
import org.dcsa.core.events.service.EquipmentEventService;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class EquipmentEventServiceImpl extends ExtendedBaseServiceImpl<EquipmentEventRepository, EquipmentEvent, UUID> implements EquipmentEventService {
    private final EquipmentEventRepository equipmentEventRepository;
    private final TransportCallService transportCallService;
    private final TransportCallTOService transportCallTOService;

    @Override
    public EquipmentEventRepository getRepository() {
        return equipmentEventRepository;
    }

    //Overriding base method here, as it marks empty results as an error, meaning we can't use switchOnEmpty()
    @Override
    public Mono<EquipmentEvent> findById(UUID id) {
        return getRepository().findById(id);
    }


    @Override
    public Mono<EquipmentEvent> loadRelatedEntities(EquipmentEvent event) {
        return mapTransportCall(event)
                .flatMap(equipmentEvent ->
                        transportCallService.findReferencesForTransportCallID(event.getTransportCallID())
                                .doOnNext(equipmentEvent::setReferences)
                                .then(transportCallService.findDocumentReferencesForTransportCallID(event.getTransportCallID()))
                                .doOnNext(equipmentEvent::setDocumentReferences)
                                .thenReturn(equipmentEvent)
                );
    }

    private Mono<EquipmentEvent> mapTransportCall(EquipmentEvent equipmentEvent){
        return transportCallTOService
                .findById(equipmentEvent.getTransportCallID())
                .doOnNext(equipmentEvent::setTransportCall)
                .thenReturn(equipmentEvent);
    }
}
