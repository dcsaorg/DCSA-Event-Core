package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Address;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

public interface AddressRepository extends ReactiveCrudRepository<Address, UUID> {

    Mono<Address> findByNameAndStreetAndStreetNumberAndFloorAndPostalCodeAndCityAndStateRegionAndCountry(
            String name,
            String street,
            String streetNumber,
            String floor,
            String postalCode,
            String city,
            String stateRegion,
            String country
    );

    default Mono<Address> findByContent(Address address) {
        if (address.getId() != null) {
            return findById(address.getId());
        }
        return findByNameAndStreetAndStreetNumberAndFloorAndPostalCodeAndCityAndStateRegionAndCountry(
                address.getName(),
                address.getStreet(),
                address.getStreetNumber(),
                address.getFloor(),
                address.getPostalCode(),
                address.getCity(),
                address.getStateRegion(),
                address.getCountry()
        );
    }

  default Mono<Address> findByIdOrEmpty(UUID id) {
    if (Objects.isNull(id)) {
      return Mono.empty();
    } else {
      return findById(id);
    }
  }
}
