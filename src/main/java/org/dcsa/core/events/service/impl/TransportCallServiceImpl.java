package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Service
public class TransportCallServiceImpl extends ExtendedBaseServiceImpl<TransportCallRepository, TransportCall, String> implements TransportCallService {
    private final ReferenceRepository referenceRepository;
    private final TransportCallRepository transportCallRepository;

    @Override
    public TransportCallRepository getRepository() {
        return transportCallRepository;
    }

    @Override
    public Mono<List<DocumentReferenceTO>> findDocumentReferencesForTransportCallID(String transportCallID) {
        Flux<DocumentReferenceTO> bookingReferences = transportCallRepository
                .findBookingReferencesByTransportCallID(transportCallID)
                .map(bRef -> DocumentReferenceTO.of(DocumentReferenceType.BKG, bRef));
        Flux<DocumentReferenceTO> transportDocumentReferences = transportCallRepository
                .findTransportDocumentReferencesByTransportCallID(transportCallID)
                .map(tRef -> DocumentReferenceTO.of(DocumentReferenceType.TRD, tRef));
        return Flux.merge(bookingReferences, transportDocumentReferences).collectList();
    }

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

}
