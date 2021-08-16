package org.dcsa.core.events.service.impl;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import org.dcsa.core.events.model.Carrier;
import org.dcsa.core.events.repository.CarrierRepository;
import org.dcsa.core.events.repository.VesselRepository;
import org.dcsa.core.events.service.VesselService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.ValidationUtils;
import org.dcsa.core.events.model.Vessel;

@RequiredArgsConstructor
@Service
public class VesselServiceImpl extends ExtendedBaseServiceImpl<VesselRepository, Vessel, String> implements VesselService {

    private final VesselRepository vesselRepository;
    private final CarrierRepository carrierRepository;

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

    private Mono<Carrier> mapEntity(Vessel vessel){
       Function<String, Mono<Carrier>> method;
       Mono<Carrier> sdkTest = null;
        if (vessel.getVesselOperatorCarrierCode() == null) {
            throw new CreateException("Vessel Operator code list provider is required");
        }
        switch (vessel.getVesselOperatorCarrierCodeListProvider()) {
            case SMDG:
                method = carrierRepository::getCarrierBySMdgCode;
                break;
            case NMFTA:
                method =  carrierRepository::getCarrierByNmftaCode;
                break;
            default:
                throw new CreateException("Unsupported vessel operator carrier code list provider: " + vessel.getVesselOperatorCarrierCodeListProvider());
        }
        return method.apply(vessel.getVesselOperatorCarrierCode())
                .switchIfEmpty(Mono.error(new CreateException("Cannot find any facility with code "
                    + vessel.getVesselOperatorCarrierCode() +  ")")));
    }

    @Override
    public Mono<Vessel> preCreateHook(Vessel vessel) {
        return mapEntity(vessel)
                .doOnNext(carrier -> vessel.setVesselOperatorCarrierID(carrier.getId()))
                .doOnNext(carrier -> vessel.setCarrier(carrier))
                .thenReturn(vessel);
    }
}
