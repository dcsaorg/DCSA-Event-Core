package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Address;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressService {
    Mono<Address> ensureResolvable(Address address);

    Mono<Address> findByIdOrEmpty(UUID id);

    Mono<Address> findById(UUID uuid);
}
