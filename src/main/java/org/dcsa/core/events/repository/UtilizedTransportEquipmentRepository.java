package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.UtilizedTransportEquipment;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UtilizedTransportEquipmentRepository
    extends ReactiveCrudRepository<UtilizedTransportEquipment, UUID>,
        UtilizedTransportEquipmentCustomRepository {

  Mono<Void> deleteUtilizedTransportEquipmentByShipmentID(UUID shipmentID);

  Mono<UtilizedTransportEquipment> findUtilizedTransportEquipmentByShipmentID(UUID shipmentID);

  @Query(
      "SELECT DISTINCT ute.* FROM consignment_item con "
          + "JOIN shipping_instruction si ON con.shipping_instruction_id = si.id "
          + "JOIN cargo_item ci ON ci.consignment_item_id = con.id "
          + "JOIN utilized_transport_equipment ute ON ute.id = ci.utilized_transport_equipment_id "
          + "WHERE si.id = :shippingInstructionReference")
  Flux<UtilizedTransportEquipment> findUtilizedTransportEquipmentsByShippingInstructionReference(String shippingInstructionReference);

  @Query(
      "SELECT DISTINCT ute.equipment_reference FROM utilized_transport_equipment ute "
          + "JOIN shipment s "
          + "ON ute.shipment_id = s.id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findEquipmentReferenceByCarrierBookRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT ute.equipment_reference FROM utilized_transport_equipment ute "
          + "JOIN cargo_item ci "
          + "ON ci.utilized_transport_equipment_id = ute.id"
          + "LEFT JOIN reference r "
          + "ON r.shipment_id = ute.shipment_id "
          + "WHERE (ci.shipping_instruction_id = :shippingInstructionReference OR r.shipping_instruction_id = :shippingInstructionReference")
  Flux<String> findEquipmentReferenceByShippingInstructionReference(
      String shippingInstructionReference);

  @Query(
      "SELECT DISTINCT ute.equipment_reference FROM utilized_transport_equipment ute "
          + "JOIN cargo_item ci "
          + "ON ci.utilized_transport_equipment_id = ute.id"
          + "LEFT JOIN reference r "
          + "ON r.shipment_id = ute.shipment_id "
          + "JOIN transport_document td "
          + "ON (td.shipping_instruction_id = ci.shipping_instruction_id OR td.shipping_instruction_id = r.shipping_instruction_id) "
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findEquipmentReferenceByTransportDocumentRef(String transportDocumentRef);
}
