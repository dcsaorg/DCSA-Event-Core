package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.ShipmentLocation;
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

  @Query("""
    SELECT DISTINCT sl.* FROM shipment_location sl
    LEFT JOIN booking b ON sl.booking_id = b.id
    JOIN shipment s ON (sl.booking_id = b.id) OR (sl.shipment_id = s.id AND sl.booking_id IS NULL)
    JOIN consignment_item ci ON s.id = ci.shipment_id
    JOIN transport_document td ON td.shipping_instruction_id = ci.shipping_instruction_id
    WHERE td.id = :transport_document_id
  """)
  Flux<ShipmentLocation> findByTransportDocumentID(UUID transportDocumentId);
}
