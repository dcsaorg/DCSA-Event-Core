package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.mapper.LocationMapper;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.repository.LocationRepository;
import org.dcsa.core.events.repository.UnLocationRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.FacilityService;
import org.dcsa.core.events.service.LocationService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.dcsa.core.exception.GetException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class LocationServiceImpl implements LocationService {

  private final FacilityService facilityService;
  private final AddressService addressService;

  private final LocationRepository locationRepository;
  private final UnLocationRepository unLocationRepository;

  private final LocationMapper locationMapper;


  public Mono<LocationTO> findPaymentLocationByShippingInstructionReference(String shippingInstructionReference) {
    return locationRepository
        .findPaymentLocationByShippingInstructionReference(shippingInstructionReference)
        .flatMap(this::getLocationTO);
  }

  @Override
  public Mono<LocationTO> ensureResolvable(LocationTO locationTO) {
    Mono<LocationTO> locationTOMono = ensureUnLocationResolvable(locationTO);

    Address address = locationTO.getAddress();
    if (address != null) {
      locationTOMono =
        locationTOMono
          .flatMap(loc ->
            addressService
                .ensureResolvable(address)
                .doOnNext(locationTO::setAddress)
                .thenReturn(locationTO)
          );
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
        .flatMap(locationRepository::findByContent)
        .switchIfEmpty(
            Mono.defer(() -> locationRepository.save(locationMapper.dtoToLocation(locationTO))))
        .doOnNext(loc -> locationTO.setId(loc.getId()))
        .map(
            location ->
                locationMapper.locationToDTO(
                    location, locationTO.getAddress(), locationTO.getFacility()));
  }

  @Override
  public Mono<LocationTO> findTOById(String locationID) {
    return locationRepository.findById(locationID)
            .switchIfEmpty(Mono.error(new GetException("Cannot find location with ID: " + locationID)))
            .flatMap(this::getLocationTO);
  }

  @Override
  public Mono<LocationTO> fetchLocationByID(String id) {
    if (id == null) return Mono.empty();
    return locationRepository
        .findById(id)
        .flatMap(this::getLocationTO);
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  @Override
  public Mono<LocationTO> fetchLocationDeepObjByID(String id) {
    if (id == null) return Mono.empty();
    return locationRepository
        .findById(id)
        .flatMap(
            location ->
                Mono.zip(
                        addressService
                            .findByIdOrEmpty(location.getAddressID())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.of(new Address())),
                        facilityService
                            .findByIdOrEmpty(location.getFacilityID())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.of(new Facility())))
                    .flatMap(
                        t2 ->
                            Mono.just(
                                locationMapper.locationToDTO(
                                    location, t2.getT1().get(), t2.getT2().get()))))
        .onErrorReturn(new LocationTO());
  }

  @Override
  public Mono<LocationTO> createLocationByTO(
      LocationTO locationTO, Function<String, Mono<Boolean>> updateEDocumentation) {

    if (Objects.isNull(locationTO)) {
      return Mono.empty();
    }

    Location location = locationMapper.dtoToLocation(locationTO);
    Mono<LocationTO> mono = ensureUnLocationResolvable(locationTO);

    if (Objects.isNull(locationTO.getAddress())) {
      return mono.flatMap(loc ->
        locationRepository
          .findByContent(locationTO)
          .switchIfEmpty(Mono.defer(() -> locationRepository.save(location)))
          .flatMap(l -> updateEDocumentation.apply(l.getId()).thenReturn(l))
          .map(locationMapper::locationToDTO));
    } else {
      return mono.flatMap(loc ->
        addressService
          .ensureResolvable(locationTO.getAddress())
          .flatMap(
              a -> {
                location.setAddressID(a.getId());
                locationTO.setAddress(a);
                return locationRepository
                    .findByContent(locationTO)
                    .switchIfEmpty(Mono.defer(() -> locationRepository.save(location)))
                    .flatMap(l -> updateEDocumentation.apply(l.getId()).thenReturn(l))
                    .map(l -> locationMapper.locationToDTO(l, a, null));
              }));
    }
  }

  @Override
  public Mono<LocationTO> resolveLocationByTO(
      String currentLocationIDInEDocumentation,
      LocationTO locationTO,
      Function<String, Mono<Boolean>> updateEDocumentationCallback) {

    // locationTO is the location received from the update eDocumentation request
    if (Objects.isNull(locationTO)) {
      if (StringUtils.isEmpty(currentLocationIDInEDocumentation)) {
        // it's possible that there may be no location linked to eDocumentation
        return Mono.empty();
      } else {
        return locationRepository.deleteById(currentLocationIDInEDocumentation).then(Mono.empty());
      }
    } else {
      return this.ensureResolvable(locationTO)
          .flatMap(lTO -> updateEDocumentationCallback.apply(lTO.getId()).thenReturn(lTO));
    }
  }

  @SuppressWarnings("OptionalGetWithoutIsPresent")
  private Mono<LocationTO> getLocationTO(Location location) {
    return Mono.zip(
            addressService
                .findByIdOrEmpty(location.getAddressID())
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty()),
            facilityService
                .findByIdOrEmpty(location.getFacilityID())
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty()))
        .flatMap(
            t2 ->
                Mono.just(
                    locationMapper.locationToDTO(
                        location,
                        t2.getT1().isPresent()
                            ? t2.getT1().get()
                            : null, // if optional is empty no value is retrieved hence in that case
                                    // we need to pass null
                        t2.getT2().isPresent() ? t2.getT2().get() : null)));
  }

  private Mono<LocationTO> ensureUnLocationResolvable(LocationTO locationTO) {
  return Mono.justOrEmpty(locationTO.getUnLocationCode())
    .flatMap(unLocationRepository::findById)
    .switchIfEmpty(Mono.error(ConcreteRequestErrorMessageException.invalidParameter(
                  "UnLocation with unLocationCode "
                + locationTO.getUnLocationCode() + " not part of reference implementation data set")))
    .thenReturn(locationTO);
  }
}
