package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.repository.AddressRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.exception.NotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AddressServiceImpl implements AddressService {
  private final AddressRepository addressRepository;

  @Override
  public Mono<Address> ensureResolvable(Address address) {
      return addressRepository.findByContent(address)
              .switchIfEmpty(Mono.defer(() -> addressRepository.save(address)));
  }

  @Override
  public Mono<Address> findByIdOrEmpty(UUID id) {
    return addressRepository.findByIdOrEmpty(id);
  }

  @Override
  public Mono<Address> findById(UUID uuid) {
      return addressRepository.findById(uuid)
              .switchIfEmpty(Mono.error(new NotFoundException("Address with id " + uuid + " missing")));
  }

}
