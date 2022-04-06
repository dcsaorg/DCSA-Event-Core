package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Reference;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ReferenceRepository extends ReactiveCrudRepository<Reference, UUID> {

  @Query(
    "SELECT reference.* "
      + "FROM reference "
      + "WHERE shipping_instruction_id = :shippingInstructionID "
      + "OR shipment_id IN ( SELECT s.id from shipment s "
      + "JOIN consignment_item ci ON ci.shipment_id = s.id "
      + "WHERE ci.shipping_instruction_id = :shippingInstructionID ) ")
  Flux<Reference> findByShippingInstructionID(UUID shippingInstructionID);

  @Query(
      "SELECT reference.* "
          + "FROM reference "
          + "WHERE shipment_id = :shipmentID "
          + "OR shipping_instruction_id IN ( SELECT si.id from shipping_instruction si "
          + "JOIN consignment_item ci ON ci.shipping_instruction_id = si.id "
          + "WHERE ci.shipment_id = :shipmentID ) ")
  Flux<Reference> findByShipmentID(UUID shipmentID);

  @Query(
      "SELECT reference.* "
          + "FROM reference "
          + "JOIN shipping_instruction si ON shipping_instruction_id = si.id "
          + "JOIN transport_document td ON td.shipping_instruction_id = si.id "
          + "WHERE  td.transport_document_reference = :transportDocumentReference "
          + "OR shipment_id IN ( SELECT s.id from shipment s "
          + "JOIN consignment_item ci ON ci.shipment_id = s.id "
          + "JOIN shipping_instruction si ON ci.shipping_instruction_id = si.id "
          + "JOIN transport_document td2 ON  td2.shipping_instruction_id = si.id "
          + "WHERE td2.transport_document_reference = :transportDocumentReference ) ")
  Flux<Reference> findByTransportDocumentReference(String transportDocumentReference);

  @Query(
      "SELECT reference.* FROM reference "
          + "LEFT JOIN shipment s ON shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingReference "
          + "OR shipping_instruction_id IN ( SELECT si.id from shipping_instruction si "
          + "JOIN consignment_item ci ON ci.shipping_instruction_id = si.id "
          + "JOIN shipment s ON ci.shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingReference)")
  Flux<Reference> findByCarrierBookingReference(String carrierBookingReference);

  Flux<Reference> findByBookingID(UUID bookingID);

  Mono<Void> deleteByConsignmentItemID(UUID consignmentItemId);

  Mono<Void> deleteByBookingID(UUID bookingID);

  Mono<Void> deleteByShippingInstructionID(UUID shippingInstructionID);
}
