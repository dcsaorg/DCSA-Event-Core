package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Equipment;
import org.dcsa.core.events.model.EquipmentEvent;
import org.dcsa.core.events.model.UnmappedEvent;
import org.dcsa.core.events.repository.EquipmentEventRepository;
import org.dcsa.core.events.repository.EquipmentRepository;
import org.dcsa.core.events.repository.UnmappedEventRepository;
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
    private final EquipmentRepository equipmentRepository;
    private final TransportCallService transportCallService;
    private final TransportCallTOService transportCallTOService;
    private final UnmappedEventRepository unmappedEventRepository;

    @Override
    protected EquipmentEventRepository getRepository() {
        return equipmentEventRepository;
    }

    @Override
    public Mono<EquipmentEvent> findById(UUID id) {
        return equipmentEventRepository.findById(id);
    }


    @Override
    public Mono<EquipmentEvent> loadRelatedEntities(EquipmentEvent event) {
        return mapTransportCall(event)
                .flatMap(equipmentEvent ->
                        transportCallService.findReferencesForTransportCallID(event.getTransportCallID())
                                .doOnNext(equipmentEvent::setReferences)
                                .then(transportCallService.findDocumentReferencesForTransportCallID(event.getTransportCallID()))
                                .doOnNext(equipmentEvent::setDocumentReferences)
                                .then(Mono.justOrEmpty(event.getEquipmentReference()))
                                .flatMap(equipmentReference -> transportCallService.findSealsForTransportCallIDAndEquipmentReference(event.getTransportCallID(), equipmentReference))
                                .doOnNext(equipmentEvent::setSeals)
                                .flatMap(sealList ->
                                        equipmentRepository.findByEquipmentReference(equipmentEvent.getEquipmentReference())
                                                .map(Equipment::getIsoEquipmentCode))
                                .doOnNext(equipmentEvent::setIsoEquipmentCode)
                                .thenReturn(equipmentEvent)
                );
    }

    private Mono<EquipmentEvent> mapTransportCall(EquipmentEvent equipmentEvent) {
        return transportCallTOService
                .findById(equipmentEvent.getTransportCallID())
                .doOnNext(equipmentEvent::setTransportCall)
                .thenReturn(equipmentEvent);
    }

    @Override
    public Mono<EquipmentEvent> create(EquipmentEvent equipmentEvent) {
        return equipmentEventRepository.save(equipmentEvent).flatMap(
                ee -> {
                    UnmappedEvent unmappedEvent = new UnmappedEvent();
                    unmappedEvent.setNewRecord(true);
                    unmappedEvent.setEventID(ee.getEventID());
                    unmappedEvent.setEnqueuedAtDateTime(ee.getEventCreatedDateTime());
                    return unmappedEventRepository.save(unmappedEvent);
                }).thenReturn(equipmentEvent);
    }
}
