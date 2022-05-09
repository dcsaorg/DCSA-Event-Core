package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.*;
import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentLocationTO;
import org.dcsa.core.events.edocumentation.repository.RequestedEquipmentRepository;
import org.dcsa.core.events.edocumentation.repository.ShipmentCutOffTimeRepository;
import org.dcsa.core.events.edocumentation.repository.ShipmentLocationRepository;
import org.dcsa.core.events.edocumentation.repository.ShipmentTransportRepository;
import org.dcsa.core.events.edocumentation.service.CarrierClauseService;
import org.dcsa.core.events.edocumentation.service.ChargeService;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.ShipmentLocation;
import org.dcsa.core.events.model.ShippingInstruction;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.dcsa.core.events.model.mapper.RequestedEquipmentMapper;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.DocumentPartyService;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.events.service.ShipmentEventService;
import org.dcsa.core.events.service.ShipmentLocationService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.util.MappingUtils;
import org.dcsa.skernel.model.Location;
import org.dcsa.skernel.model.mapper.LocationMapper;
import org.dcsa.skernel.model.transferobjects.LocationTO;
import org.dcsa.skernel.repositority.LocationRepository;
import org.dcsa.skernel.repositority.VesselRepository;
import org.dcsa.skernel.service.AddressService;
import org.dcsa.skernel.service.LocationService;
import org.dcsa.skernel.service.VesselService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class ShipmentLocationServiceImpl implements ShipmentLocationService {

  // repositories
  private final LocationRepository locationRepository;
  private final ShipmentLocationRepository shipmentLocationRepository;

  // mappers
  private final LocationMapper locationMapper;
  private final ShipmentMapper shipmentMapper;

  // services
  private final LocationService locationService;
  private final AddressService addressService;

  @Override
  public Mono<List<ShipmentLocationTO>> fetchShipmentLocationsByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.empty();
    return shipmentLocationRepository
        .findByBookingID(bookingID)
        .flatMap(getDeepShipmentLocationObject)
        .collectList();
  }

  @Override
  public Mono<List<ShipmentLocationTO>> fetchShipmentLocationByTransportDocumentID(
      UUID transportDocumentId) {
    return shipmentLocationRepository
        .findByTransportDocumentID(transportDocumentId)
        .flatMap(getDeepShipmentLocationObject)
        .collectList();
  }

    private final Function<ShipmentLocation, Mono<ShipmentLocationTO>>
      getDeepShipmentLocationObject =
          shipmentLocation -> locationService
              .fetchLocationDeepObjByID(shipmentLocation.getLocationID())
              .flatMap(
                  locationTO -> {
                    ShipmentLocationTO shipmentLocationTO =
                        shipmentMapper.shipmentLocationToDTO(shipmentLocation);
                    shipmentLocationTO.setLocation(locationTO);
                    return Mono.just(shipmentLocationTO);
                  });

  @Override
  public Mono<List<ShipmentLocationTO>> createShipmentLocationsByBookingIDAndTOs(
      UUID bookingID, List<ShipmentLocationTO> shipmentLocations) {

    if (Objects.isNull(shipmentLocations) || shipmentLocations.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }

    return Flux.fromIterable(shipmentLocations)
        .flatMap(
            slTO -> {
              ShipmentLocation shipmentLocation = new ShipmentLocation();
              shipmentLocation.setBookingID(bookingID);
              shipmentLocation.setShipmentLocationTypeCode(slTO.getShipmentLocationTypeCode());
              shipmentLocation.setDisplayedName(slTO.getDisplayedName());
              shipmentLocation.setEventDateTime(slTO.getEventDateTime());

              Location location = locationMapper.dtoToLocation(slTO.getLocation());

              if (Objects.isNull(slTO.getLocation().getAddress())) {
                return locationRepository
                    .save(location)
                    .map(
                        l -> {
                          LocationTO lTO = locationMapper.locationToDTO(l);
                          shipmentLocation.setLocationID(l.getId());
                          return Tuples.of(lTO, shipmentLocation);
                        });
              } else {
                return addressService
                    .ensureResolvable(slTO.getLocation().getAddress())
                    .flatMap(
                        a -> {
                          location.setAddressID(a.getId());
                          return locationRepository
                              .save(location)
                              .map(
                                  l -> {
                                    LocationTO lTO = locationMapper.locationToDTO(l);
                                    lTO.setAddress(a);
                                    shipmentLocation.setLocationID(l.getId());
                                    return Tuples.of(lTO, shipmentLocation);
                                  });
                        });
              }
            })
        .flatMap(
            t ->
                shipmentLocationRepository
                    .save(t.getT2())
                    .map(
                        savedSl -> {
                          ShipmentLocationTO shipmentLocationTO = new ShipmentLocationTO();
                          shipmentLocationTO.setLocation(t.getT1());
                          shipmentLocationTO.setShipmentLocationTypeCode(
                              savedSl.getShipmentLocationTypeCode());
                          shipmentLocationTO.setDisplayedName(savedSl.getDisplayedName());
                          shipmentLocationTO.setEventDateTime(savedSl.getEventDateTime());
                          return shipmentLocationTO;
                        }))
        .collectList();
  }
}
