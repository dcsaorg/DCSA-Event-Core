package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.ShipmentLocation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentLocationRepository extends ReactiveCrudRepository<ShipmentLocation, UUID> {
  Flux<ShipmentLocation> findByBookingID(UUID bookingID);

  Flux<ShipmentLocation> findByShipmentID(UUID shipmentID);

  Mono<Void> deleteByBookingID(UUID bookingID);

  @Query(
      "SELECT sl.* FROM booking b "
          + "JOIN transport_document td ON td.transport_document_reference = b.transport_document_reference "
          + "JOIN shipment_location sl ON sl.booking_id = b.id "
          + "WHERE td.id = :transportDocumentId")
  Flux<ShipmentLocation> findByTransportDocumentID(UUID transportDocumentId);
}
