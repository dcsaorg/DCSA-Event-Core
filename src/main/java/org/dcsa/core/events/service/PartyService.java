package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.PartyTO;
import reactor.core.publisher.Mono;

public interface PartyService {
    @Deprecated
    Mono<PartyTO> ensureResolvable(PartyTO partyTO);
    Mono<PartyTO> findTOById(String publisherID);
}
