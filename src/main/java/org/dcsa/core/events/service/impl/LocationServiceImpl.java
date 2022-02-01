package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.mappers.LocationMapper;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.repository.AddressRepository;
import org.dcsa.core.events.repository.FacilityRepository;
import org.dcsa.core.events.repository.LocationRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.FacilityService;
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class LocationServiceImpl extends ExtendedBaseServiceImpl<LocationRepository, Location, String> implements LocationService {
  private final LocationRepository locationRepository;
  private final FacilityRepository facilityRepository;
  private final AddressRepository addressRepository;

  private final FacilityService facilityService;
  private final AddressService addressService;

  private final LocationMapper locationMapper;

  @Override
  public LocationRepository getRepository() {
    return locationRepository;
  }

  public Mono<LocationTO> findPaymentLocationByShippingInstructionID(String shippingInstructionID) {
    return locationRepository
        .findPaymentLocationByShippingInstructionID(shippingInstructionID)
        .flatMap(this::getLocationTO);
  }

  @Override
  public Mono<LocationTO> ensureResolvable(LocationTO locationTO) {
    Address address = locationTO.getAddress();
    Mono<LocationTO> locationTOMono;
    if (address != null) {
      locationTOMono =
          addressService
              .ensureResolvable(address)
              .doOnNext(locationTO::setAddress)
              .thenReturn(locationTO);
    } else {
      locationTOMono = Mono.just(locationTO);
    }
    if (locationTO.getFacilityCode() != null) {
      locationTOMono =
          locationTOMono
              .flatMap(
                  loc ->
                      facilityService.findByUNLocationCodeAndFacilityCode(
                          loc.getUnLocationCode(),
                          loc.getFacilityCodeListProvider(),
                          loc.getFacilityCode()))
              .doOnNext(locationTO::setFacility)
              .thenReturn(locationTO);
    }

    return locationTOMono
        .flatMap(
            locTo ->
                Util.createOrFindByContent(
                    locTo,
                    locationRepository::findByContent,
                    locTO -> this.create(locTO.toLocation())))
        .map(location -> location.toLocationTO(locationTO.getAddress(), locationTO.getFacility()));
  }

  @Override
  public Mono<LocationTO> findTOById(String locationID) {
    return findById(locationID).flatMap(this::getLocationTO);
  }

  @Override
  public Mono<Optional<LocationTO>> fetchLocationByID(String id) {
    if (id == null) return Mono.just(Optional.empty());
    return locationRepository
        .findById(id)
        .flatMap(this::getLocationTO)
        .onErrorReturn(new LocationTO())
        .map(Optional::of);
  }

  @Override
  public Mono<Optional<LocationTO>> createLocationByTO(LocationTO locationTO, Function<String, Mono<Boolean>> updateeDocumentation) {

    if (Objects.isNull(locationTO)) {
      return Mono.just(Optional.empty());
    }

    Location location = locationMapper.dtoToLocation(locationTO);

    if (Objects.isNull(locationTO.getAddress())) {
      return locationRepository
              .save(location)
              .flatMap(l -> updateeDocumentation.apply(l.getId()).thenReturn(l))
              .map(locationMapper::locationToDTO)
              .map(Optional::of);
    } else {
      return addressService
              .ensureResolvable(locationTO.getAddress())
              .flatMap(a -> {
                location.setAddressID(a.getId());
                return locationRepository
                        .save(location)
                        .flatMap(l -> updateeDocumentation.apply(l.getId()).thenReturn(l))
                        .map(l -> {
                          LocationTO lTO = locationMapper.locationToDTO(l);
                          lTO.setAddress(a);
                          return lTO;
                        })
                        .map(Optional::of);
              });
    }
  }

  @Override
  public Mono<Optional<LocationTO>> resolveLocationByTO(String currentLocationIDIneDocumentation, LocationTO locationTO, Function<String, Mono<Boolean>> updateeDocumentationCallback) {

    // locationTO is the location received from the update eDocumentation request
    if (Objects.isNull(locationTO)) {
      if (StringUtils.isEmpty(currentLocationIDIneDocumentation)) {
        // it's possible that there may be no location linked to eDocumentation
        return Mono.just(Optional.empty());
      } else {
        return locationRepository
                .deleteById(currentLocationIDIneDocumentation)
                .then(Mono.just(Optional.empty()));
      }
    } else {
      return this
              .ensureResolvable(locationTO)
              .flatMap(lTO -> updateeDocumentationCallback.apply(lTO.getId()).thenReturn(lTO))
              .map(Optional::of);
    }
  }

  private Mono<LocationTO> getLocationTO(Location location) {
    return Mono.zip(
            addressRepository
                .findByIdOrEmpty(location.getAddressID())
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty()),
            facilityRepository
                .findByIdOrEmpty(location.getFacilityID())
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty()))
        .flatMap(
            t2 -> {
              LocationTO locTO = locationMapper.locationToDTO(location);
              t2.getT1().ifPresent(locTO::setAddress);
              t2.getT2().ifPresent(locTO::setFacility);
              return Mono.just(locTO);
            });
  }
}
