package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.VesselPosition;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.VesselPositionTO;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface VesselPositionService extends ExtendedBaseService<VesselPosition, String> {
    Flux<VesselPosition> findAllById(Iterable<String> ids);
    Mono<VesselPositionTO> ensureResolvable(VesselPositionTO partyTO);
}
