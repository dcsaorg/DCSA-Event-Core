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
public class VoyageServiceImpl implements VoyageService {

    private final VoyageRepository voyageRepository;

    public Mono<Voyage> findByCarrierVoyageNumberAndServiceID(String carrierVoyageNumber, UUID serviceID) {
        return voyageRepository.findByCarrierVoyageNumberAndServiceID(carrierVoyageNumber, serviceID);
    }

    @Override
    public Mono<Voyage> create(Voyage importVoyage) {
        return voyageRepository.save(importVoyage);
    }
}
