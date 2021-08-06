package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.base.AbstractLocation;
import org.dcsa.core.events.model.base.AbstractParty;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.repository.OperationsEventRepository;
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.events.service.OperationsEventService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.MappingUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationsEventServiceImpl extends ExtendedBaseServiceImpl<OperationsEventRepository, OperationsEvent, UUID> implements OperationsEventService {

    private final OperationsEventRepository operationsEventRepository;
    private final TransportCallTOService transportCallTOService;
    private final PartyService partyService;
    private final LocationService locationService;

    @Override
    public OperationsEventRepository getRepository() {
        return operationsEventRepository;
    }

    @Override
    public Mono<OperationsEvent> loadRelatedEntities(OperationsEvent operationsEvent) {
        return Flux.concat(
                getAndSetTransportCall(operationsEvent),
                getAndSetPublisher(operationsEvent),
                getAndSetEventLocation(operationsEvent),
                getAndSetVesselPosition(operationsEvent)
        ).then(Mono.just(operationsEvent));
    }

    private Mono<OperationsEvent> getAndSetTransportCall(OperationsEvent operationsEvent) {
        if (operationsEvent.getTransportCallID() == null) return Mono.empty();
        return transportCallTOService
                .findById(operationsEvent.getTransportCallID())
                .doOnNext(transportCall -> operationsEvent.setTransportCall(MappingUtils.instanceFrom(transportCall, TransportCallTO::new, AbstractTransportCall.class)))
                .thenReturn(operationsEvent);
    }
    private Mono<OperationsEvent> getAndSetPublisher(OperationsEvent operationsEvent) {
        if (operationsEvent.getPublisherID() == null) return Mono.empty();
        return partyService
                .findById(operationsEvent.getPublisherID())
                .doOnNext(party -> operationsEvent.setPublisher(MappingUtils.instanceFrom(party, PartyTO::new, AbstractParty.class)))
                .thenReturn(operationsEvent);
    }

    private Mono<OperationsEvent> getAndSetEventLocation(OperationsEvent operationsEvent) {
        if (operationsEvent.getEventLocationID() == null) return Mono.empty();
        return locationService
                .findById(operationsEvent.getEventLocationID())
                .doOnNext(location -> operationsEvent.setEventLocation(MappingUtils.instanceFrom(location, LocationTO::new, AbstractLocation.class)))
                .thenReturn(operationsEvent);
    }

    private Mono<OperationsEvent> getAndSetVesselPosition(OperationsEvent operationsEvent) {
        if (operationsEvent.getVesselPositionID() == null) return Mono.empty();
        return locationService
                .findById(operationsEvent.getVesselPositionID())
                .doOnNext(location -> operationsEvent.setVesselPosition(MappingUtils.instanceFrom(location, LocationTO::new, AbstractLocation.class)))
                .thenReturn(operationsEvent);
    }
}
