package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Address;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AddressService extends ExtendedBaseService<Address, UUID> {
    Mono<Address> ensureResolvable(Address address);
}
