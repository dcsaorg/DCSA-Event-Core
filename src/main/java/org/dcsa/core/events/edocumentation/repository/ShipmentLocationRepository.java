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
    SELECT sl.* FROM
      (SELECT s.* FROM dcsa_im_v3_0.transport_document td
        JOIN dcsa_im_v3_0.consignment_item ci ON td.shipping_instruction_id = ci.shipping_instruction_id
        JOIN dcsa_im_v3_0.shipment s ON s.id = ci.shipment_id
        WHERE td.id = :transportDocumentId LIMIT 1) as s
    LEFT JOIN dcsa_im_v3_0.booking b ON b.id = s.booking_id
    JOIN dcsa_im_v3_0.shipment_location sl ON (sl.booking_id = b.id) OR (sl.shipment_id = s.id AND sl.booking_id IS NULL)
  """)
  Flux<ShipmentLocation> findByTransportDocumentID(UUID transportDocumentId);
}
