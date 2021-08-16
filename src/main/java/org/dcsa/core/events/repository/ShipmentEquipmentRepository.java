package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentEquipment;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ShipmentEquipmentRepository extends ExtendedRepository<ShipmentEquipment, UUID> {

  @Query(
      "SELECT DISTINCT se.equipment_reference FROM shipment_equipment se "
          + "JOIN shipment s "
          + "ON se.shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findEquipmentReferenceByCarrierBookRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT se.equipment_reference FROM shipment_equipment se "
          + "JOIN shipment s "
          + "ON se.shipment_id = s.id "
          + "JOIN cargo_item ci "
          + "ON ci.shipment_id = s.id"
          + "LEFT JOIN references r "
          + "ON r.shipment_id = s.id "
          + "WHERE (ci.shipping_instruction_id = :shippingInstructionID OR r.shipping_instruction_id = :shippingInstructionID")
  Flux<String> findEquipmentReferenceByShippingInstructionID(String shippingInstructionID);

  @Query(
      "SELECT DISTINCT se.equipment_reference FROM shipment_equipment se "
          + "JOIN shipment s "
          + "ON se.shipment_id = s.id "
          + "JOIN cargo_item ci "
          + "ON ci.shipment_id = s.id"
          + "LEFT JOIN references r "
          + "ON r.shipment_id = s.id "
          + "JOIN transport_document td "
          + "ON (td.shipping_instruction_id = ci.shipping_instruction_id OR td.shipping_instruction_id = r.shipping_instruction_id) "
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findEquipmentReferenceByTransportDocumentRef(String transportDocumentRef);
}
