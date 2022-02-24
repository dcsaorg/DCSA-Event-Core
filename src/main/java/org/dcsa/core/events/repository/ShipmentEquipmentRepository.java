package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentEquipment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ShipmentEquipmentRepository extends ReactiveCrudRepository<ShipmentEquipment, UUID>, ShipmentEquipmentCustomRepository {

  Mono<Void> deleteShipmentEquipmentByShipmentID(UUID shipmentID);

  Mono<ShipmentEquipment> findShipmentEquipmentByShipmentID(UUID shipmentID);

  Flux<ShipmentEquipment> findAllByShipmentIDIn(List<UUID> shipmentIDs);

  Mono<ShipmentEquipment> findByEquipmentReference(String equipmentReference);

  Mono<Void> deleteByEquipmentReferenceInAndShipmentIDIn(
      List<String> equipmentReferences, List<UUID> shipmentIDs);

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
          + "WHERE (ci.shipping_instruction_id = :shippingInstructionID OR r.shipping_instruction_id = :shippingInstructionID")
  Flux<String> findEquipmentReferenceByShippingInstructionID(String shippingInstructionID);

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
