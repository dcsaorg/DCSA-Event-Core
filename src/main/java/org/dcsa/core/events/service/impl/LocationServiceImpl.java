package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.mappers.LocationMapper;
import org.dcsa.core.events.model.transferobjects.LocationTO;
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
public class LocationServiceImpl
    extends ExtendedBaseServiceImpl<LocationRepository, Location, String>
    implements LocationService {

  private final FacilityService facilityService;
  private final AddressService addressService;

  private final LocationRepository locationRepository;

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
  public Mono<LocationTO> fetchLocationByID(String id) {
    if (id == null) return Mono.empty();
    return locationRepository
        .findById(id)
        .flatMap(this::getLocationTO)
        .onErrorReturn(new LocationTO());
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
                            .defaultIfEmpty(Optional.empty()),
                        facilityService
                            .findByIdOrEmpty(location.getFacilityID())
                            .map(Optional::of)
                            .defaultIfEmpty(Optional.empty()))
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

    if (Objects.isNull(locationTO.getAddress())) {
      return locationRepository
          .save(location)
          .flatMap(l -> updateEDocumentation.apply(l.getId()).thenReturn(l))
          .map(locationMapper::locationToDTO);
    } else {
      return addressService
          .ensureResolvable(locationTO.getAddress())
          .flatMap(
              a -> {
                location.setAddressID(a.getId());
                return locationRepository
                    .save(location)
                    .flatMap(l -> updateEDocumentation.apply(l.getId()).thenReturn(l))
                    .map(l -> locationMapper.locationToDTO(l, a, null));
              });
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
                    locationMapper.locationToDTO(location, t2.getT1().get(), t2.getT2().get())));
  }
}
