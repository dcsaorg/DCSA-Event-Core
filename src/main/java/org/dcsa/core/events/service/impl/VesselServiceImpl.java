package org.dcsa.core.events.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.CarrierCodeListProvider;
import org.dcsa.core.events.service.CarrierService;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
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
import org.dcsa.core.extendedrequest.ExtendedParameters;

@RequiredArgsConstructor
@Service
public class VesselServiceImpl extends ExtendedBaseServiceImpl<VesselRepository, Vessel, String> implements VesselService {

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
    public Mono<Vessel> findById(final String VesselIMONumber) {
        ExtendedRequest<Vessel> extendedRequest = newExtendedRequest();
        extendedRequest.parseParameter(Map.of("vesselIMONumber", List.of(String.valueOf(VesselIMONumber))));
        return vesselRepository.findAllExtended(extendedRequest)
                .collectList()
                .flatMap(vessels -> {
                    if(vessels.isEmpty()){
                        return Mono.error(new NotFoundException("Cannot find any vessel operator with provided VesselIMONumber: "
                                + VesselIMONumber ));
                    }
                    return Mono.just(vessels.get(0));
                });
    }

    private Mono<Carrier> mapEntity(Vessel vessel) {
        CarrierCodeListProvider carrierCodeListProvider = vessel.getVesselOperatorCarrierCodeListProvider();
        String carrierCode = vessel.getVesselOperatorCarrierCode();
        if (carrierCodeListProvider == null) {
            throw new CreateException("Vessel Operator code list provider is required");
        }
        return carrierService.findByCode(carrierCodeListProvider, carrierCode);
    }

    @Override
    public Mono<Vessel> preCreateHook(Vessel vessel) {
        return mapEntity(vessel)
                .doOnNext(carrier -> vessel.setVesselOperatorCarrierID(carrier.getId()))
                .doOnNext(vessel::setCarrier)
                .thenReturn(vessel);
    }
    @Override
    public Mono<Vessel> preUpdateHook(Vessel current, Vessel update) {
        return mapEntity(update)
                .doOnNext(carrier -> update.setVesselOperatorCarrierID(carrier.getId()))
                .doOnNext(update::setCarrier)
                .thenReturn(update);
    }
    public ExtendedRequest<Vessel> newExtendedRequest() {
        return new ExtendedRequest<>(extendedParameters, r2dbcDialect, Vessel.class);
    }
}
