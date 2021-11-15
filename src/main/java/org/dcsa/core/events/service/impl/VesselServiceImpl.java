package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Carrier;
import org.dcsa.core.events.model.Vessel;
import org.dcsa.core.events.model.enums.CarrierCodeListProvider;
import org.dcsa.core.events.repository.VesselRepository;
import org.dcsa.core.events.service.CarrierService;
import org.dcsa.core.events.service.VesselService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.ValidationUtils;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class VesselServiceImpl extends ExtendedBaseServiceImpl<VesselRepository, Vessel, UUID> implements VesselService {

    private final VesselRepository vesselRepository;
    private final CarrierService carrierService;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;

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
        //  Fails if duplicate key is created.
        // One should check first using findById before creating, return error if key(VesselIMONumber) exists.
        return preCreateHook(vessel)
                .flatMap(this::preSaveHook)
                .flatMap(vesselRepository::insert);
    }

    @Override
    public Mono<Vessel> update(Vessel vessel) {
        return findById(getIdOfEntity(vessel))
                .flatMap(current -> this.preUpdateHook(current, vessel))
                .flatMap(this::save);
    }


    @Override
    public Mono<Vessel> findById(final UUID vesselID) {
        return vesselRepository.findById(vesselID);
    }

    public Mono<Vessel> findByVesselIMONumber(final String vesselIMONumber) {
        try {
            ValidationUtils.validateVesselIMONumber(vesselIMONumber);
        } catch (IllegalArgumentException e) {
            return Mono.error(new CreateException(e.getLocalizedMessage()));
        }
        ExtendedRequest<Vessel> extendedRequest = newExtendedRequest();
        extendedRequest.parseParameter(Map.of("vesselIMONumber", List.of(vesselIMONumber)));
        return vesselRepository.findAllExtended(extendedRequest)
                .take(2)
                .collectList()
                .flatMap(vessels -> {
                    if (vessels.size() > 1) {
                        throw new AssertionError("vesselIMONumber should be unique but " + vesselIMONumber
                                + " matched more than one entity");
                    }
                    if (vessels.isEmpty()){
                        return Mono.error(new NotFoundException("Cannot find any vessel operator with provided VesselIMONumber: "
                                + vesselIMONumber ));
                    }
                    return Mono.just(vessels.get(0));
                });
    }

    private Mono<Carrier> findCarrierIfPresent(Vessel vessel) {
        CarrierCodeListProvider carrierCodeListProvider = vessel.getVesselOperatorCarrierCodeListProvider();
        String carrierCode = vessel.getVesselOperatorCarrierCode();
        if (carrierCodeListProvider == null) {
            return Mono.empty();
        }
        return carrierService.findByCode(carrierCodeListProvider, carrierCode);
    }

    @Override
    public Mono<Vessel> preCreateHook(Vessel vessel) {
        return findCarrierIfPresent(vessel)
                .doOnNext(carrier -> vessel.setVesselOperatorCarrierID(carrier.getId()))
                .doOnNext(vessel::setCarrier)
                .thenReturn(vessel);
    }
    @Override
    public Mono<Vessel> preUpdateHook(Vessel current, Vessel update) {
        return findCarrierIfPresent(update)
                .doOnNext(carrier -> update.setVesselOperatorCarrierID(carrier.getId()))
                .doOnNext(update::setCarrier)
                .thenReturn(update);
    }
    public ExtendedRequest<Vessel> newExtendedRequest() {
        return new ExtendedRequest<>(extendedParameters, r2dbcDialect, Vessel.class);
    }
}
