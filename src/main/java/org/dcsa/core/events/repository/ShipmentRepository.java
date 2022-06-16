package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Shipment;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentRepository
    extends ReactiveCrudRepository<Shipment, UUID> {

  Mono<Shipment> findByCarrierBookingReference(String carrierBookingReference);

  @Query(
    "SELECT * FROM shipment s "
      + "WHERE s.carrier_booking_reference = :carrierBookingReference "
      + "ORDER BY s.valid_until NULLS FIRST LIMIT 1")
  Mono<Shipment> findByCarrierBookingReferenceAndValidUntilIsNull(String carrierBookingReference);

  @Query(
      "SELECT COUNT(s.id) "
          + "FROM shipment s "
          + "JOIN booking b ON s.booking_id = b.id "
          + "WHERE (:documentStatus is null OR b.document_status = :documentStatus)")
  Mono<Long> countShipmentsByDocumentStatus(ShipmentEventTypeCode documentStatus);

  @Query(
      "SELECT DISTINCT s.* FROM shipment s "
          + "JOIN consignment_item ci ON ci.shipment_id = s.id  "
          + "JOIN shipping_instruction si ON si.id = ci.shipping_instruction_id "
          + "WHERE si.shipping_instruction_reference = :shippingInstructionReference")
  Flux<Shipment> findByShippingInstructionReference(String shippingInstructionReference);

  @Query("""
      SELECT DISTINCT s.carrier_booking_reference FROM shipment s
        JOIN consignment_item ci ON ci.shipment_id = s.id
        JOIN shipping_instruction si ON ci.shipping_instruction_id = si.id
        JOIN transport_document td ON si.id = td.shipping_instruction_id
       WHERE td.transport_document_reference = :transportDocumentRef
       """)
  Flux<String> findCarrierBookingRefsByTransportDocumentRef(String transportDocumentRef);

  @Query("""
      SELECT DISTINCT s.carrier_booking_reference FROM shipment s
        JOIN utilized_transport_equipment ute ON s.id = ute.shipment_id
        JOIN cargo_item ci ON ute.id = ci.utilized_transport_equipment_id
       WHERE ci.shipping_instruction_id = :shippingInstructionID
       """)
  Flux<String> findCarrierBookingRefsByShippingInstructionID(UUID shippingInstructionID);

  @Query("""
      SELECT DISTINCT s.carrier_booking_reference FROM shipment s
       JOIN shipment_transport st ON s.id = st.shipment_id
       JOIN transport t ON st.transport_id = t.id
       WHERE t.load_transport_call_id = :transportCallID
    """)
  Flux<String> findCarrierBookingRefsByTransportCallID(UUID transportCallID);

}
