package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.repository.VesselRepository;
import org.dcsa.core.events.service.VesselService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.ValidationUtils;
import org.dcsa.core.events.model.Vessel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class VesselServiceImpl extends ExtendedBaseServiceImpl<VesselRepository, Vessel, String> implements VesselService {

    private final VesselRepository vesselRepository;

    @Override
    public VesselRepository getRepository() {
        return vesselRepository;
    }

    @Override
    public Mono<Vessel> create(Vessel vessel) {
        if (vessel.getVesselIMONumber() == null) {
            return Mono.error(new CreateException("Missing vessel IMO number"));
        }
        try {
            ValidationUtils.validateVesselIMONumber(vessel.getVesselIMONumber());
        } catch (IllegalArgumentException e) {
            return Mono.error(new CreateException(e.getLocalizedMessage()));
        }
        return preCreateHook(vessel)
                .flatMap(this::preSaveHook)
                .flatMap(vesselRepository::insert);
    }
}