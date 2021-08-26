package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.TransportCallVoyage;
import org.dcsa.core.events.repository.TransportCallVoyageRepository;
import org.dcsa.core.events.service.TransportCallVoyageService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TransportCallVoyageServiceImpl extends ExtendedBaseServiceImpl<TransportCallVoyageRepository, TransportCallVoyage, String> implements TransportCallVoyageService {
    private final TransportCallVoyageRepository transportCallRepository;

    @Override
    public TransportCallVoyageRepository getRepository() {
        return transportCallRepository;
    }

    @Override
    public Mono<TransportCallVoyage> findByTransportCallIDAndVoyageID(String transportCallID, UUID voyageID) {
        return transportCallRepository.findByTransportCallIDAndVoyageID(transportCallID, voyageID);
    }

}
