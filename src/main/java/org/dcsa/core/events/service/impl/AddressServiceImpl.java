package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.repository.AddressRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl extends ExtendedBaseServiceImpl<AddressRepository, Address, UUID>
    implements AddressService {
  private final AddressRepository addressRepository;

  @Override
  public AddressRepository getRepository() {
    return addressRepository;
  }

  @Override
  public Mono<Address> ensureResolvable(Address address) {
    return Util.createOrFindByContent(address, addressRepository::findByContent, this::create);
  }

  @Override
  public Mono<Address> findByIdOrEmpty(UUID id) {
    return addressRepository.findByIdOrEmpty(id);
  }
}
