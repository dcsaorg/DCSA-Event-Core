package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.TransportCallBasedEvent;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.DocumentReferenceService;
import org.dcsa.core.events.service.TransportCallService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransportCallServiceImpl implements TransportCallService {
    private final ReferenceRepository referenceRepository;
    private final TransportCallRepository transportCallRepository;

    @Override
    public Mono<List<Reference>> findReferencesForTransportCallID(String transportCallID) {
        return transportCallRepository
                .findShipmentIDByTransportCallID(transportCallID)
                .flatMapMany(referenceRepository::findByShipmentID)
                .collectList();
    }

    @Override
    public Mono<List<Seal>> findSealsForTransportCallIDAndEquipmentReference(String transportCallID, String equipmentReference) {
        return transportCallRepository.findSealsForTransportCallIDAndEquipmentReference(transportCallID, equipmentReference)
                .collectList();
    }

    @Override
    public Mono<TransportCall> create(TransportCall transportCall) {
        return transportCallRepository.save(transportCall);
    }

}
