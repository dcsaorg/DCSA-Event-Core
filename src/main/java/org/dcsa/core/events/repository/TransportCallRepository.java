package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.enums.DCSATransportType;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransportCallRepository extends ExtendedRepository<TransportCall, UUID> {
    @Query("SELECT DISTINCT shipment_transport.shipment_id from shipment_transport shipment_transport"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE transport.load_transport_call_id = :transportCallID"
            + " OR transport.discharge_transport_call_id = :transportCallID")
    Mono<UUID> findShipmentIDByTransportCallID(UUID transportCallID);

    @Query("SELECT DISTINCT seal.* FROM seal"
            + " JOIN utilized_transport_equipment utilized_transport_equipment ON utilized_transport_equipment.id = seal.utilized_transport_equipment_id"
            + " JOIN shipment_transport shipment_transport ON shipment_transport.shipment_id = utilized_transport_equipment.shipment_id"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE utilized_transport_equipment.equipment_reference = :equipmentReference"
            + " AND (transport.load_transport_call_id = :transportCallID OR transport.discharge_transport_call_id = :transportCallID)")
    Flux<Seal> findSealsForTransportCallIDAndEquipmentReference(UUID transportCallID, String equipmentReference);

    @Query(
      "SELECT shipping_instruction.transport_document_type FROM shipping_instruction"
          + " JOIN transport_document transport_document ON transport_document.shipping_instruction_id = shipping_instruction.id"
          + " WHERE transport_document.transport_document_reference = :transportDocumentReference")
  Mono<String> findTransportDocumentTypeCodeByTransportDocumentReference(
      String transportDocumentReference);

  @Query(
      "SELECT DISTINCT voyage.carrier_voyage_number FROM voyage"
          + " JOIN transport_call ON transport_call.import_voyage_id = voyage.id OR transport_call.export_voyage_id = voyage.id"
          + " WHERE transport_call.id = :transportCallID")
  Flux<String> findCarrierVoyageNumbersByTransportCallID(UUID transportCallID);

  @Query(
      "SELECT DISTINCT service.carrier_service_code FROM service"
          + " JOIN voyage voyage ON voyage.service_id = service.id"
          + " JOIN transport_call ON transport_call.import_voyage_id = voyage.id OR transport_call.export_voyage_id = voyage.id"
          + " WHERE transport_call.id = :transportCallID")
  Flux<String> findCarrierServiceCodesByTransportCallID(UUID transportCallID);

  @Query(
      "SELECT DISTINCT tc.transport_call_reference FROM transport_call tc "
          + "JOIN transport t "
          + "ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + "ON st.transport_id = t.id "
          + "JOIN shipment s "
          + "ON s.id = st.shipment_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findTransportCallReferenceByCarrierBookingRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT tc.transport_call_reference FROM transport_call tc "
          + "JOIN transport t "
          + "ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + "ON st.transport_id = t.id "
          + "JOIN shipment s "
          + "ON s.id = st.shipment_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findTransportCallReferenceByShippingInstructionReference(String shippingInstructionReference);

  @Query(
      "SELECT DISTINCT tc.transport_call_reference FROM transport_call tc "
          + "JOIN transport t "
          + "ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + "ON st.transport_id = t.id "
          + "JOIN shipment s "
          + "ON s.id = st.shipment_id "
          + "LEFT JOIN consignment_item ci "
          + "ON ci.shipment_id = s.id"
          + "LEFT JOIN reference r "
          + "ON r.shipment_id = s.id "
          + "JOIN transport_document td "
          + "ON (td.shipping_instruction_id = ci.shipping_instruction_id OR td.shipping_instruction_id = r.shipping_instruction_id)"
          + "WHERE td.transport_document_reference = :transportDocumentRef")
  Flux<String> findTransportCallReferenceByTransportDocumentRef(String transportDocumentRef);
}
