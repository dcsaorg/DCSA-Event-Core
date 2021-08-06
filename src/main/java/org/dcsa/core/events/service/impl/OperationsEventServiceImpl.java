package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.base.AbstractLocation;
import org.dcsa.core.events.model.base.AbstractParty;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.repository.OperationsEventRepository;
import org.dcsa.core.events.service.*;
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
                transportCallTOService
                        .findById(operationsEvent.getTransportCallID())
                        .doOnNext(operationsEvent::setTransportCall)
                        .thenReturn(operationsEvent),
                partyService
                        .findById(operationsEvent.getPublisherID())
                        .doOnNext(party -> operationsEvent.setPublisher(MappingUtils.instanceFrom(party, PartyTO::new, AbstractParty.class))),
                locationService
                        .findById(operationsEvent.getEventLocationID())
                        .doOnNext(location -> operationsEvent.setEventLocation(MappingUtils.instanceFrom(location, LocationTO::new, AbstractLocation.class))),
                locationService
                        .findById(operationsEvent.getEventLocationID())
                        .doOnNext(location -> operationsEvent.setVesselPosition(MappingUtils.instanceFrom(location, LocationTO::new, AbstractLocation.class)))
        ).then(Mono.just(operationsEvent));
    }

    @Override
    public Mono<OperationsEvent> create(OperationsEvent operationsEvent) {
        operationsEvent.setTransportCallID(operationsEvent.getTransportCall().getTransportCallID());
        operationsEvent.setPublisher(operationsEvent.getPublisher());
        return super.save(operationsEvent);
    }
}
