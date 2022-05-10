package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.ShipmentMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentLocationTO;
import org.dcsa.core.events.edocumentation.repository.ShipmentLocationRepository;
import org.dcsa.core.events.model.ShipmentLocation;
import org.dcsa.core.events.model.mapper.ShipmentLocationMapper;
import org.dcsa.core.events.service.ShipmentLocationService;
import org.dcsa.skernel.service.LocationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ShipmentLocationServiceImpl implements ShipmentLocationService {

  // repositories
  private final ShipmentLocationRepository shipmentLocationRepository;

  // mappers
  private final ShipmentLocationMapper shipmentLocationMapper;
  private final ShipmentMapper shipmentMapper;

  // services
  private final LocationService locationService;

  @Override
  public Mono<List<ShipmentLocationTO>> fetchShipmentLocationsByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.empty();
    return shipmentLocationRepository
        .findByBookingID(bookingID)
        .flatMap(this::getDeepShipmentLocationObject)
        .collectList();
  }

  @Override
  public Mono<List<ShipmentLocationTO>> fetchShipmentLocationByTransportDocumentID(
      UUID transportDocumentId) {
    return shipmentLocationRepository
        .findByTransportDocumentID(transportDocumentId)
        .flatMap(this::getDeepShipmentLocationObject)
        .collectList();
  }

  private Mono<ShipmentLocationTO> getDeepShipmentLocationObject(
      ShipmentLocation shipmentLocation) {
    return locationService
        .fetchLocationDeepObjByID(shipmentLocation.getLocationID())
        .flatMap(
            locationTO -> {
              ShipmentLocationTO shipmentLocationTO =
                  shipmentMapper.shipmentLocationToDTO(shipmentLocation);
              shipmentLocationTO.setLocation(locationTO);
              return Mono.just(shipmentLocationTO);
            });
  }

  @Override
  public Mono<List<ShipmentLocationTO>> createShipmentLocationsByBookingIDAndTOs(
      UUID bookingID, List<ShipmentLocationTO> shipmentLocations) {

    if (Objects.isNull(shipmentLocations) || shipmentLocations.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }

    return Flux.fromIterable(shipmentLocations)
        .flatMap(
            shipmentLocationTO -> {
              ShipmentLocation shipmentLocation =
                  shipmentLocationMapper.dtoToShipmentLocationWithBookingID(
                      shipmentLocationTO, bookingID);

              return locationService
                  .ensureResolvable(shipmentLocationTO.getLocation())
                  .doOnNext(shipmentLocationTO::setLocation)
                  .doOnNext(locationTO -> shipmentLocation.setLocationID(locationTO.getId()))
                  .then(shipmentLocationRepository.save(shipmentLocation))
                  .thenReturn(shipmentLocationTO);
            })
        .collectList();
  }
}
