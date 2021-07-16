package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.VesselPosition;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.VesselPositionTO;
import org.dcsa.core.events.repository.PartyRepository;
import org.dcsa.core.events.repository.VesselPositionRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.events.service.VesselPositionService;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class VesselPositionServiceImpl extends ExtendedBaseServiceImpl<VesselPositionRepository, VesselPosition, String> implements VesselPositionService {

    private final VesselPositionRepository vesselPositionRepository;

    @Override
    public VesselPositionRepository getRepository() {
        return vesselPositionRepository;
    }

    public Flux<VesselPosition> findAllById(Iterable<String> ids) {
        return vesselPositionRepository.findAllById(ids);
    }

    @Override
    public Mono<VesselPositionTO> ensureResolvable(VesselPositionTO vesselPositionTO) {
        return Mono.just(vesselPositionTO)
                .flatMap(pTo -> Util.createOrFindByContent(
                        pTo,
                        vesselPositionRepository::findByContent,
                        pTO -> this.create(pTO.toVesselPosition())
                )).map(VesselPosition::toVesselPositionTO);
    }
}
