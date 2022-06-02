package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportCallServiceImpl implements TransportCallService {
    private final ReferenceRepository referenceRepository;
    private final TransportCallRepository transportCallRepository;

    @Override
    public Mono<List<Reference>> findReferencesForTransportCallID(UUID transportCallID) {
        return transportCallRepository
                .findShipmentIDByTransportCallID(transportCallID)
                .flatMapMany(referenceRepository::findByShipmentID)
                .collectList();
    }

    @Override
    public Mono<List<Seal>> findSealsForTransportCallIDAndEquipmentReference(UUID transportCallID, String equipmentReference) {
        return transportCallRepository.findSealsForTransportCallIDAndEquipmentReference(transportCallID, equipmentReference)
                .collectList();
    }

    @Override
    public Mono<TransportCall> create(TransportCall transportCall) {
        return transportCallRepository.save(transportCall);
    }

}
