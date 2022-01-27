package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PartyService extends ExtendedBaseService<Party, String> {
    Flux<Party> findAllById(Iterable<String> ids);

    @Deprecated
    Mono<PartyTO> ensureResolvable(PartyTO partyTO);
    Mono<PartyTO> findTOById(String publisherID);
}
