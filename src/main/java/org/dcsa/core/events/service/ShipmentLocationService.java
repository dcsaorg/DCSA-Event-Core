package org.dcsa.core.events.service;

import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentLocationTO;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ShipmentLocationService {

  Mono<List<ShipmentLocationTO>> fetchShipmentLocationsByBookingID(UUID bookingID);

  Mono<List<ShipmentLocationTO>> fetchShipmentLocationByTransportDocumentID(
      UUID transportDocumentId);

  Mono<List<ShipmentLocationTO>> createShipmentLocationsByBookingIDAndTOs(
      final UUID bookingID, List<ShipmentLocationTO> shipmentLocations);
}
