package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.*;
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
    private final TransportCallVoyageService transportCallVoyageService;
    private final ModeOfTransportRepository modeOfTransportRepository;
    private final VesselRepository vesselRepository;
    private final VesselService vesselService;
    private final VoyageRepository voyageRepository;
    private final VoyageService voyageService;
    private final ServiceRepository serviceRepository;
    private final ServiceService serviceService;
    private final TransportCallService transportCallService;
    private final ExtendedParameters extendedParameters;
    private final R2dbcDialect r2dbcDialect;

    @Override
    public Flux<TransportCallTO> findAll() {
        ExtendedRequest<TransportCallTO> extendedRequest = newExtendedRequest();
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
                        return Mono.empty();
                    }
                    TransportCallTO transportCallTO = transportCallTOs.get(0);

                    return modeOfTransportRepository
                            .findByTransportCallID(transportCallTO.getTransportCallID())
                            .map(ModeOfTransport::getDcsaTransportType)
                            .doOnNext(transportCallTO::setModeOfTransport)
                            // Ensure non-empty (ModeOfTransport can be null)
                            .thenReturn(transportCallTO)
                            .flatMap(
                                    ignored -> voyageRepository.findByTransportCallID(transportCallTO.getTransportCallID()))
                            .doOnNext(voyage -> transportCallTO.setCarrierVoyageNumber(voyage.getCarrierVoyageNumber()))
                            .flatMap(voyage -> Mono.justOrEmpty(voyage.getServiceID()))
                            .flatMap(serviceRepository::findById)
                            .map(org.dcsa.core.events.model.Service::getCarrierServiceCode)
                            .doOnNext(transportCallTO::setCarrierServiceCode)
                            .thenReturn(transportCallTO);
                });
    }

    @Override
    public Mono<TransportCallTO> create(TransportCallTO transportCallTO) {
        if (transportCallTO.getCarrierVoyageNumber() == null ^ transportCallTO.getCarrierServiceCode() == null) {
            if (transportCallTO.getCarrierServiceCode() == null) {
                throw new IllegalArgumentException("Cannot create transport call where voyage code is present but service code is missing");
            }
            throw new IllegalArgumentException("Cannot create transport call where service code is present but voyage code is missing");
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
                        vesselService.findById(vessel.getVesselIMONumber())
                        .onErrorResume(NotFoundException.class,
                                (e) -> carrierService.findByCode(vessel.getVesselOperatorCarrierCodeListProvider(), vessel.getVesselOperatorCarrierCode())
                                .flatMap(carrier -> {
                                    vessel.setCarrier(carrier);
                                    return vesselService.create(vessel);
                                })))
                .doOnNext(transportCallTO::setVessel)
                // Force a non-empty Mono
                .thenReturn(transportCallTO)
                .flatMap(ignored ->
                        facilityService.findByUNLocationCodeAndFacilityCode(
                                transportCallTO.getUNLocationCode(),
                                transportCallTO.getFacilityCodeListProvider(),
                                transportCallTO.getFacilityCode()
                        ).doOnNext(facility -> transportCallTO.setFacilityID(facility.getFacilityID()))
                ).map(ignored -> {
                    TransportCall transportCall = MappingUtils.instanceFrom(transportCallTO, TransportCall::new, AbstractTransportCall.class);
                    // When we receive an event via subscription, we need to preserve the original Transport ID.
                    if (transportCallTO.getTransportCallID() != null) {
                        transportCall.setNewRecord(true);
                    }
                    return transportCall;
                })
                .flatMap(transportCallService::create)
                .doOnNext(transportCall -> transportCallTO.setTransportCallID(transportCall.getTransportCallID()))
                .flatMap(ignored -> Mono.justOrEmpty(transportCallTO.getCarrierServiceCode()))
                .flatMap(carrierServiceCode ->
                    serviceService.findByCarrierServiceCode(carrierServiceCode)
                            .switchIfEmpty(Mono.defer(() -> createService(carrierServiceCode, transportCallTO.getVessel()))
                )).flatMap(service -> voyageService.findByCarrierVoyageNumber(transportCallTO.getCarrierVoyageNumber())
                        .switchIfEmpty(Mono.defer(() -> {
                            Voyage voyage = new Voyage();
                            voyage.setCarrierVoyageNumber(transportCallTO.getCarrierVoyageNumber());
                            voyage.setServiceID(service.getId());
                            return voyageService.create(voyage);
                })))
                .flatMap(voyage -> transportCallVoyageService.findByTransportCallIDAndVoyageID(transportCallTO.getTransportCallID(), voyage.getId())
                        .switchIfEmpty(Mono.defer(() -> {
                            TransportCallVoyage transportCallVoyage = new TransportCallVoyage();
                            transportCallVoyage.setTransportCallID(transportCallTO.getTransportCallID());
                            transportCallVoyage.setVoyageID(voyage.getId());
                            return transportCallVoyageService.create(transportCallVoyage);
                })))
                .thenReturn(transportCallTO);
    }

    private Mono<Service> createService(String carrierServiceCode, Vessel vessel) {
        return carrierService.findByCode(
                vessel.getVesselOperatorCarrierCodeListProvider(),
                vessel.getVesselOperatorCarrierCode()
        ).flatMap(carrier -> {
            Service service = new Service();
            service.setCarrierServiceCode(carrierServiceCode);
            service.setCarrierID(carrier.getId());
            return serviceService.create(service);
        });
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
