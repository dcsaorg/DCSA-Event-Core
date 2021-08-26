package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Voyage;
import org.dcsa.core.events.repository.VoyageRepository;
import org.dcsa.core.events.service.VoyageService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VoyageServiceImpl extends ExtendedBaseServiceImpl<VoyageRepository, Voyage, UUID> implements VoyageService {

    private final VoyageRepository voyageRepository;

    @Override
    public VoyageRepository getRepository() {
        return voyageRepository;
    }

    public Mono<Voyage> findByTransportCallID(String transportCallID) {
        return voyageRepository.findByTransportCallID(transportCallID);
    }

    public Mono<Voyage> findByCarrierVoyageNumber(String carrierVoyageNumber) {
        return voyageRepository.findByCarrierVoyageNumber(carrierVoyageNumber);
    }
}
