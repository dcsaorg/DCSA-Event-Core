package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReferenceRepository extends ReactiveCrudRepository<Reference, UUID> {

  @Query(
      "SELECT reference.* "
          + "FROM reference "
          + "WHERE shipping_instruction_id = :shippingInstructionReference "
          + "OR shipment_id IN ( SELECT s.id from shipment s "
          + "JOIN utilized_transport_equipment ute ON s.id = ute.shipment_id "
          + "JOIN cargo_item ci ON ci.utilized_transport_equipment_id = ute.id "
          + "WHERE ci.shipping_instruction_id = :shippingInstructionReference ) ")
  Flux<Reference> findByShippingInstructionReference(String shippingInstructionReference);

  @Query(
      "SELECT reference.* "
          + "FROM reference "
          + "WHERE shipment_id = :shipmentID "
          + "OR shipping_instruction_id IN ( SELECT si.id from shipping_instruction si "
          + "JOIN cargo_item ci ON ci.shipping_instruction_id = si.id "
          + "JOIN utilized_transport_equipment ute ON ute.id = ci.utilized_transport_equipment_id "
          + "WHERE ute.shipment_id = :shipmentID ) ")
  Flux<Reference> findByShipmentID(UUID shipmentID);

  @Query(
      "SELECT reference.* "
          + "FROM reference "
          + "JOIN shipping_instruction si ON shipping_instruction_id = si.id "
          + "JOIN transport_document td ON td.shipping_instruction_id = si.id "
          + "WHERE  td.transport_document_reference = :transportDocumentReference "
          + "OR shipment_id IN ( SELECT s.id from shipment s "
          + "JOIN utilized_transport_equipment ute ON ute.shipment_id = s.id "
          + "JOIN cargo_item ci ON ci.utilized_transport_equipment_id = ute.id "
          + "JOIN shipping_instruction si ON ci.shipping_instruction_id = si.id "
          + "JOIN transport_document td2 ON  td2.shipping_instruction_id = si.id "
          + "WHERE td2.transport_document_reference = :transportDocumentReference ) ")
  Flux<Reference> findByTransportDocumentReference(String transportDocumentReference);

  @Query(
      "SELECT reference.* FROM reference "
          + "LEFT JOIN shipment s ON shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingReference "
          + "OR shipping_instruction_id IN ( SELECT si.id from shipping_instruction si "
          + "JOIN cargo_item ci ON ci.shipping_instruction_id = si.id "
          + "JOIN utilized_transport_equipment ute ON s.id = ute.shipment_id "
          + "JOIN shipment s ON ute.shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingReference)")
  Flux<Reference> findByCarrierBookingReference(String carrierBookingReference);

  Flux<Reference> findByBookingID(UUID bookingID);

  Flux<Reference> findByCargoItemID(UUID cargoItemID);

  Mono<Void> deleteByBookingID(UUID bookingID);

  Mono<Void> deleteByShippingInstructionReference(String shippingInstructionReference);
}
