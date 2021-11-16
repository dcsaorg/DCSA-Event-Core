package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.*;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.NotFoundException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.MappingUtils;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class TransportCallTOServiceImpl extends ExtendedBaseServiceImpl<TransportCallTORepository, TransportCallTO, String> implements TransportCallTOService {
    private final CarrierService carrierService;
    private final FacilityService facilityService;
    private final LocationService locationService;
    private final TransportCallTORepository transportCallTORepository;
    private final ModeOfTransportRepository modeOfTransportRepository;
    private final VesselService vesselService;
    private final VoyageService voyageService;
    private final ServiceService serviceService;
    private final TransportCallService transportCallService;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;

    @Override
    public Flux<TransportCallTO> findAll() {
        return findAllExtended(newExtendedRequest());
    }
    @Override
    public Flux<TransportCallTO> findAllExtended(ExtendedRequest<TransportCallTO> extendedRequest) {
        return transportCallTORepository.findAllExtended(extendedRequest);
    }

    @Override
    public Mono<TransportCallTO> findById(String id) {
        ExtendedRequest<TransportCallTO> extendedRequest = newExtendedRequest();
        extendedRequest.parseParameter(Map.of("transportCallID", List.of(id)));
        return transportCallTORepository.findAllExtended(extendedRequest)
                .take(2)
                .collectList()
                .flatMap(transportCallTOs -> {
                    if (transportCallTOs.size() > 1) {
                        throw new AssertionError("transportID is not unique");
                    }
                    if (transportCallTOs.isEmpty()) {
                        return Mono.error(new NotFoundException("Not transport call with ID " + id + " was found"));
                    }
                    TransportCallTO transportCallTO = transportCallTOs.get(0);
                    return Mono.just(transportCallTO);
                });
    }

    @Override
    public Mono<TransportCallTO> create(TransportCallTO transportCallTO) {
        if (transportCallTO.getExportVoyage() == null) {
            throw new CreateException("Cannot create transport call where export voyage number is missing");
        }
        if (transportCallTO.getImportVoyage() == null) {
            throw new CreateException("Cannot create transport call where export voyage number is missing");
        }
        if (transportCallTO.getCarrierServiceCode() == null) {
                throw new CreateException("Cannot create transport call where service code is missing");
        }
        // FIXME: Assert that import and export carrier service code is the same
        if (transportCallTO.getFacilityCode() == null ^ transportCallTO.getFacilityCodeListProvider() == null) {
            if (transportCallTO.getFacilityCode() == null) {
                throw new CreateException("Cannot create transport call where facility code list provider is present but facility code is missing");
            }
            throw new CreateException("Cannot create transport call where facility code is present but facility code list provider is missing");
        }
        if (transportCallTO.getFacilityCode() == null && transportCallTO.getUNLocationCode() != null) {
            LocationTO location = transportCallTO.getLocation();
            if (location == null) {
                location = new LocationTO();
                transportCallTO.setLocation(location);
            }
            if (location.getUnLocationCode() != null && !location.getUnLocationCode().equals(transportCallTO.getUNLocationCode())) {
                throw new CreateException("Cannot create transport call where UN Location Code (on TC) does not match the UN Location Code in the location (and there is no facility code)");
            }
            location.setUnLocationCode(transportCallTO.getUNLocationCode());
        }
        return Mono.justOrEmpty(transportCallTO.getLocation())
                .flatMap(locationService::ensureResolvable)
                .doOnNext(loc -> transportCallTO.setLocationID(loc.getId()))
                // Force a non-empty Mono
                .thenReturn(transportCallTO.getModeOfTransport())
                .flatMap(modeOfTransportRepository::findByDcsaTransportType)
                .switchIfEmpty(Mono.error(new IllegalStateException("Unknown DCSA Transport type: "
                        + transportCallTO.getModeOfTransport()
                        + " (is data loaded correctly into the database?")))
                .doOnNext(modeOfTransport -> transportCallTO.setModeOfTransportID(modeOfTransport.getId()))
                .then(Mono.justOrEmpty(transportCallTO.getVessel()))
                .flatMap(vessel ->
                        vesselService.findByVesselIMONumber(vessel.getVesselIMONumber())
                        .onErrorResume(NotFoundException.class, (e) -> {
                            if (vessel.getVesselOperatorCarrierCodeListProvider() == null ^ vessel.getVesselOperatorCarrierCode() == null) {
                                if (vessel.getVesselOperatorCarrierCodeListProvider() == null) {
                                    throw new IllegalArgumentException("Cannot create vessel where operator carrier code list provider is missing but vessel operator carrier code is present");
                                }
                                throw new IllegalArgumentException("Cannot create vessel where vessel operator carrier code is missing but operator carrier code list provider is present");
                            }
                            if (vessel.getVesselOperatorCarrierCode() == null) {
                                return vesselService.create(vessel);
                            }
                            return carrierService.findByCode(vessel.getVesselOperatorCarrierCodeListProvider(), vessel.getVesselOperatorCarrierCode())
                                    .flatMap(carrier -> {
                                        vessel.setCarrier(carrier);
                                        return vesselService.create(vessel);
                                    });
                        }))
                .doOnNext(transportCallTO::setVessel)
                // If there is a facility
                .then(Mono.justOrEmpty(transportCallTO.getFacilityCode()))
                .flatMap(ignored ->
                        facilityService.findByUNLocationCodeAndFacilityCode(
                                transportCallTO.getUNLocationCode(),
                                transportCallTO.getFacilityCodeListProvider(),
                                transportCallTO.getFacilityCode()
                        ).doOnNext(facility -> transportCallTO.setFacilityID(facility.getFacilityID()))
                )
                // Force a non-empty Mono
                .thenReturn(transportCallTO)
                .flatMap(ignored -> Mono.justOrEmpty(transportCallTO.getCarrierServiceCode()))
                .flatMap(carrierServiceCode ->
                        serviceService.findByCarrierServiceCode(carrierServiceCode)
                                .switchIfEmpty(Mono.defer(() -> createService(carrierServiceCode, transportCallTO.getVessel())))
                                .doOnNext(service -> {
                                    transportCallTO.getExportVoyage().getService().setId(service.getId());
                                    transportCallTO.getImportVoyage().getService().setId(service.getId());
                                    transportCallTO.getExportVoyage().setServiceID(service.getId());
                                    transportCallTO.getImportVoyage().setServiceID(service.getId());
                                })
                ).flatMap(service -> voyageService.findByCarrierVoyageNumberAndServiceID(transportCallTO.getImportVoyageNumber(), service.getId())
                        .switchIfEmpty(Mono.defer(() -> voyageService.create(transportCallTO.getImportVoyage())))
                        .doOnNext(voyage -> transportCallTO.setImportVoyageID(voyage.getId()))
                        .then(voyageService.findByCarrierVoyageNumberAndServiceID(transportCallTO.getExportVoyageNumber(), service.getId()))
                        .switchIfEmpty(Mono.defer(() -> voyageService.create(transportCallTO.getExportVoyage())))
                        .doOnNext(voyage -> transportCallTO.setExportVoyageID(voyage.getId()))
                )
                .switchIfEmpty(Mono.error(new AssertionError("Internal error: Voyage creation should have been non-empty")))
                .map(ignored -> {
                    TransportCall transportCall = MappingUtils.instanceFrom(transportCallTO, TransportCall::new, AbstractTransportCall.class);
                    // When we receive an event via subscription, we need to preserve the original Transport ID.
                    if (transportCallTO.getTransportCallID() != null) {
                        transportCall.setNewRecord(true);
                    }
                    return transportCall;
                })
                .switchIfEmpty(Mono.error(new AssertionError("Internal error: TransportCallTO was empty")))
                .flatMap(transportCallService::create)
                .doOnNext(transportCall -> transportCallTO.setTransportCallID(transportCall.getTransportCallID()))
                .switchIfEmpty(Mono.error(new AssertionError("Internal error: Post create transport call was empty but should not be")))
                .thenReturn(transportCallTO);
    }

    private Mono<Service> createService(String carrierServiceCode, Vessel vessel) {
        Service service = new Service();
        service.setCarrierServiceCode(carrierServiceCode);
        Mono<Service> carrierMono = Mono.just(service);
        if (vessel.getVesselOperatorCarrierCode() != null ||vessel.getVesselOperatorCarrierCodeListProvider() != null) {
            carrierMono = carrierService.findByCode(
                    vessel.getVesselOperatorCarrierCodeListProvider(),
                    vessel.getVesselOperatorCarrierCode()
            ).map(Carrier::getId)
             .doOnNext(service::setCarrierID)
            .thenReturn(service);
        }
        return carrierMono.flatMap(serviceService::create);
    }

    @Override
    public TransportCallTORepository getRepository() {
        return transportCallTORepository;
    }

    @Override
    public String getIdOfEntity(TransportCallTO entity) {
        return entity.getTransportCallID();
    }


    public ExtendedRequest<TransportCallTO> newExtendedRequest() {
        return new ExtendedRequest<>(extendedParameters, r2dbcDialect, TransportCallTO.class);
    }
}
