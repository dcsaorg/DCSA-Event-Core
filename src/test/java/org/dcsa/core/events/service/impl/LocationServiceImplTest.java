package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.mappers.LocationMapper;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.repository.AddressRepository;
import org.dcsa.core.events.repository.FacilityRepository;
import org.dcsa.core.events.repository.LocationRepository;
import org.dcsa.core.events.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for LocationService implementation")
public class LocationServiceImplTest {

  @Mock LocationRepository locationRepository;

  @Mock AddressRepository addressRepository;
  @Mock FacilityRepository facilityRepository;

  @Mock
    AddressService addressService;

  @InjectMocks LocationServiceImpl locationService;

  @Spy LocationMapper locationMapper = Mappers.getMapper(LocationMapper.class);

  LocationTO locationTO;
  Location location;
  Address address;
  Facility facility;

  private final Mono<Boolean> doNothing = Mono.just(true);

  @BeforeEach
  void init() {

    initEntities();

    initTO();
  }

  private void initEntities() {
    address = new Address();
    address.setId(UUID.fromString("8fecc6d0-2a78-401d-948a-b9753f6b53d5"));
    address.setName("Fraz");
    address.setStreet("Kronprinsessegade");
    address.setPostalCode("1306");
    address.setCity("København");
    address.setCountry("Denmark");

    facility = new Facility();
    facility.setFacilityID(UUID.randomUUID());
    facility.setFacilityName("Some facility");
    facility.setFacilityBICCode("x".repeat(4));

    location = new Location();
    location.setId(UUID.randomUUID().toString());
    location.setLocationName("Location Name");
    location.setUnLocationCode("x".repeat(5));
    location.setLongitude("x".repeat(10));
    location.setLatitude("x".repeat(11));
    location.setAddressID(address.getId());
    location.setFacilityID(facility.getFacilityID());
  }

  private void initTO() {
    locationTO = locationMapper.locationToDTO(location);
    locationTO.setAddress(address);
    locationTO.setFacility(facility);
  }

  @Test
  @DisplayName("Test create locationTO without address")
  void testCreateLocationByTOWithoutAddress() {

    locationTO.setAddress(null);

    when(locationRepository.save(any())).thenReturn(Mono.just(location));

    StepVerifier.create(locationService.createLocationByTO(locationTO, x -> doNothing))
        .assertNext(
            l -> {
              assertTrue(l.isPresent());

              verify(addressService,times(0)).ensureResolvable(any());

              assertEquals(facility.getFacilityID(), l.get().getFacilityID());
              assertEquals(address.getId(), l.get().getAddressID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create locationTO with address")
  void testCreateLocationByTOWithAddress() {

    when(locationRepository.save(any())).thenReturn(Mono.just(location));
    when(addressService.ensureResolvable(any())).thenReturn(Mono.just(address));

    StepVerifier.create(locationService.createLocationByTO(locationTO, x -> doNothing))
        .assertNext(
            l -> {
              assertTrue(l.isPresent());

              assertEquals(facility.getFacilityID(), l.get().getFacilityID());
              assertEquals(address.getId(), l.get().getAddressID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch optional locationTO by ID with facility and address")
  void testFetchLocationTOByIDWithAddressAndFacility() {

    String locationID = UUID.randomUUID().toString();
    location.setId(locationID);

    when(locationRepository.findById(any(String.class))).thenReturn(Mono.just(location));
    when(addressRepository.findByIdOrEmpty(any())).thenReturn(Mono.just(address));
    when(facilityRepository.findByIdOrEmpty(any())).thenReturn(Mono.just(facility));

    StepVerifier.create(locationService.fetchLocationByID(location.getId()))
        .assertNext(
            l -> {
              assertTrue(l.isPresent());

              verify(addressRepository).findByIdOrEmpty(any());
              verify(facilityRepository).findByIdOrEmpty(any());

              assertEquals(facility.getFacilityID(), l.get().getFacilityID());
              assertEquals(address.getId(), l.get().getAddressID());
              assertNotNull(l.get().getAddress());
              assertNotNull(l.get().getFacility());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch optional locationTO by ID without facility and address")
  void testFetchLocationTOByIDWithoutAddressAndFacility() {

    String locationID = UUID.randomUUID().toString();
    location.setId(locationID);
    location.setAddressID(null);
    location.setFacilityID(null);

    when(locationRepository.findById(any(String.class))).thenReturn(Mono.just(location));

    StepVerifier.create(locationService.fetchLocationByID(location.getId()))
        .assertNext(
            l -> {
              assertTrue(l.isPresent());

              assertNull(l.get().getFacilityID());
              assertNull(l.get().getAddressID());

              assertNull(l.get().getFacility());
              assertNull(l.get().getAddress());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch optional locationTO by ID without facility and address")
  void testFetchLocationWithNullID() {

    StepVerifier.create(locationService.fetchLocationByID(null))
        .assertNext(
            l -> {
              assertFalse(l.isPresent());
            })
        .verifyComplete();
  }
}
