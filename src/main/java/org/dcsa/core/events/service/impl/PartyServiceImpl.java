package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.repository.PartyRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class PartyServiceImpl extends ExtendedBaseServiceImpl<PartyRepository, Party, String> implements PartyService {
    private final AddressService addressService;
    private final PartyRepository partyRepository;

    @Override
    public PartyRepository getRepository() {
        return partyRepository;
    }

    public Flux<Party> findAllById(Iterable<String> ids) {
        return partyRepository.findAllById(ids);
    }

    @Override
    public Mono<PartyTO> ensureResolvable(PartyTO partyTO) {
        Address address = partyTO.getAddress();
        Mono<PartyTO> partyTOMono;
        if (address != null) {
            partyTOMono = addressService.ensureResolvable(address)
                    .doOnNext(partyTO::setAddress)
                    .thenReturn(partyTO);
        } else {
            partyTOMono = Mono.just(partyTO);
        }

        return partyTOMono
                .flatMap(pTo -> Util.createOrFindByContent(
                        pTo,
                        partyRepository::findByContent,
                        pTO -> this.create(pTO.toParty())
                )).map(party -> party.toPartyTO(partyTO.getAddress()));
    }
}