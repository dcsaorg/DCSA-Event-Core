package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.UnmappedEvent;
import org.dcsa.core.events.repository.OperationsEventRepository;
import org.dcsa.core.events.repository.UnmappedEventRepository;
import org.dcsa.core.events.service.*;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
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
    private final TimestampDefinitionService timestampDefinitionService;
    private final UnmappedEventRepository unmappedEventRepository;

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
        if (operationsEvent.getTransportCallID() == null) return Mono.just(operationsEvent);
        return transportCallTOService
                .findById(operationsEvent.getTransportCallID())
                .doOnNext(operationsEvent::setTransportCall)
                .thenReturn(operationsEvent);
    }
    
    private Mono<OperationsEvent> getAndSetPublisher(OperationsEvent operationsEvent) {
        if (operationsEvent.getPublisherID() == null) return Mono.just(operationsEvent);
        return partyService
                .findTOById(operationsEvent.getPublisherID())
                .doOnNext(operationsEvent::setPublisher)
                .thenReturn(operationsEvent);
    }

    private Mono<OperationsEvent> getAndSetEventLocation(OperationsEvent operationsEvent) {
        if (operationsEvent.getEventLocationID() == null) return Mono.just(operationsEvent);
        return locationService
                .findTOById(operationsEvent.getEventLocationID())
                .doOnNext(operationsEvent::setEventLocation)
                .thenReturn(operationsEvent);
    }

    private Mono<OperationsEvent> getAndSetVesselPosition(OperationsEvent operationsEvent) {
        if (operationsEvent.getVesselPositionID() == null) return Mono.just(operationsEvent);
        return locationService
                .findTOById(operationsEvent.getVesselPositionID())
                .doOnNext(operationsEvent::setVesselPosition)
                .thenReturn(operationsEvent);
    }

  @Override
  public Mono<OperationsEvent> create(OperationsEvent operationsEvent) {
      return Mono.justOrEmpty(operationsEvent.getEventLocation())
              .flatMap(locationService::ensureResolvable)
              .doOnNext(loc -> operationsEvent.setEventLocationID(loc.getId()))
              .then(Mono.justOrEmpty(operationsEvent.getVesselPosition()))
              .flatMap(locationService::ensureResolvable)
              .doOnNext(loc -> operationsEvent.setVesselPositionID(loc.getId()))
              .then(Mono.justOrEmpty(operationsEvent.getPublisher()))
              .flatMap(partyService::ensureResolvable)
              .doOnNext(publisher -> operationsEvent.setPublisherID(publisher.getId()))
              .thenReturn(operationsEvent)
              .flatMap(oe -> {
                  try {
                      oe.ensurePhaseTypeIsDefined();
                  } catch (IllegalStateException e) {
                      return Mono.error(new CreateException("Cannot derive portCallPhaseTypeCode automatically from this timestamp. Please define it explicitly"));
                  }
                  return Mono.just(oe);
              })
              .flatMap(super::create)
              .flatMap(timestampDefinitionService::markOperationsEventAsTimestamp)
              .flatMap(
                      ope -> {
                          UnmappedEvent unmappedEvent = new UnmappedEvent();
                          unmappedEvent.setNewRecord(true);
                          unmappedEvent.setEventID(ope.getEventID());
                          unmappedEvent.setEnqueuedAtDateTime(ope.getEventCreatedDateTime());
                          return unmappedEventRepository.save(unmappedEvent)
                                  .thenReturn(ope);
                      })
              ;
  }
}
