package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.UtilizedTransportEquipment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UtilizedTransportEquipmentRepository extends ReactiveCrudRepository<UtilizedTransportEquipment, UUID>, UtilizedTransportEquipmentCustomRepository {

  Mono<Void> deleteUtilizedTransportEquipmentByShipmentID(UUID shipmentID);

  Mono<UtilizedTransportEquipment> findUtilizedTransportEquipmentByShipmentID(UUID shipmentID);

  @Query(
      "SELECT DISTINCT se.equipment_reference FROM shipment_equipment se "
          + "JOIN shipment s "
          + "ON se.shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findEquipmentReferenceByCarrierBookRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT se.equipment_reference FROM shipment_equipment se "
          + "JOIN cargo_item ci "
          + "ON ci.shipment_equipment_id = se.id"
          + "LEFT JOIN reference r "
          + "ON r.shipment_id = se.shipment_id "
          + "WHERE (ci.shipping_instruction_id = :shippingInstructionReference OR r.shipping_instruction_id = :shippingInstructionReference")
  Flux<String> findEquipmentReferenceByShippingInstructionReference(String shippingInstructionReference);

  @Query(
      "SELECT DISTINCT se.equipment_reference FROM shipment_equipment se "
          + "JOIN cargo_item ci "
          + "ON ci.shipment_equipment_id = se.id"
          + "LEFT JOIN reference r "
          + "ON r.shipment_id = se.shipment_id "
          + "JOIN transport_document td "
          + "ON (td.shipping_instruction_id = ci.shipping_instruction_id OR td.shipping_instruction_id = r.shipping_instruction_id) "
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findEquipmentReferenceByTransportDocumentRef(String transportDocumentRef);
}
