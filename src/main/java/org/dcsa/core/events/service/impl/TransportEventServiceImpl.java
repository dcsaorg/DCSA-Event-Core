package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.events.repository.TransportEventRepository;
import org.dcsa.core.events.service.TransportEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportEventServiceImpl extends ExtendedBaseServiceImpl<TransportEventRepository, TransportEvent, UUID> implements TransportEventService {
    private final TransportEventRepository transportEventRepository;
    private final TransportCallService transportCallService;

    @Autowired
    private TransportCallRepository transportCallRepository;

    @Autowired
    private TransportCallTOService transportCallTOService;

    @Autowired
    private ReferenceRepository referenceRepository;


    @Override
    public TransportEventRepository getRepository() {
        return transportEventRepository;
    }

    //Overriding base method here, as it marks empty results as an error, meaning we can't use switchOnEmpty()
    @Override
    public Mono<TransportEvent> findById(UUID id) {
        return getRepository().findById(id);
    }

    @Override
    public Mono<TransportEvent> mapTransportCall(TransportEvent transportEvent){
        return transportCallTOService
                .findById(transportEvent.getTransportCallID())
                .doOnNext(transportEvent::setTransportCall)
                .thenReturn(transportEvent);
    }

    @Override
    public Mono<TransportEvent> mapReferences(TransportEvent transportEvent) {
        Flux<Reference> references = transportCallRepository
                .findShipmentIDByTransportCallID(transportEvent.getTransportCallID())
                .flatMapMany(referenceRepository::findByShipmentID);
        return references.collectList()
                .doOnNext(transportEvent::setReferences)
                .thenReturn(transportEvent);
    }

    @Override
    public Mono<TransportEvent> mapDocumentReferences(TransportEvent transportEvent) {
        Flux<DocumentReferenceTO> bookingReferences = transportCallRepository
                .findBookingReferencesByTransportCallID(transportEvent.getTransportCallID())
                .map(bRef -> DocumentReferenceTO.of(DocumentReferenceType.BKG, bRef));
        Flux<DocumentReferenceTO> transportDocumentReferences = transportCallRepository
                .findTransportDocumentReferencesByTransportCallID(transportEvent.getTransportCallID())
                .map(tRef -> DocumentReferenceTO.of(DocumentReferenceType.TRD, tRef));
        return Flux.merge(bookingReferences, transportDocumentReferences).collectList()
                .doOnNext(transportEvent::setDocumentReferences)
                .thenReturn(transportEvent);
    }
}
