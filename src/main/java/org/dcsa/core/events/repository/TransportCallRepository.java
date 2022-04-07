package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TransportCallRepository extends ExtendedRepository<TransportCall, String> {
    @Query("SELECT DISTINCT shipment_transport.shipment_id from shipment_transport shipment_transport"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE transport.load_transport_call_id = :transportCallID"
            + " OR transport.discharge_transport_call_id = :transportCallID")
    Mono<UUID> findShipmentIDByTransportCallID(String transportCallID);

    @Query("SELECT DISTINCT shipment.carrier_booking_reference from shipment shipment"
            + " JOIN shipment_transport shipment_transport ON shipment_transport.shipment_id = shipment.id"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE transport.load_transport_call_id = :transportCallID"
            + " OR transport.discharge_transport_call_id = :transportCallID")
    Flux<String> findBookingReferencesByTransportCallID(String transportCallID);

    @Query("SELECT DISTINCT transport_document.transport_document_reference from transport_document transport_document"
            + " JOIN consignment_item consignment_item ON consignment_item.shipping_instruction_id = transport_document.shipping_instruction_id"
            + " JOIN shipment_transport shipment_transport ON shipment_transport.shipment_id = consignment_item.shipment_id"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE transport.load_transport_call_id = :transportCallID"
            + " OR transport.discharge_transport_call_id = :transportCallID")
    Flux<String> findTransportDocumentReferencesByTransportCallID(String transportCallID);

    @Query("SELECT DISTINCT seal.* FROM seal"
            + " JOIN utilized_transport_equipment utilized_transport_equipment ON utilized_transport_equipment.id = seal.utilized_transport_equipment_id"
            + " JOIN shipment_transport shipment_transport ON shipment_transport.shipment_id = utilized_transport_equipment.shipment_id"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE utilized_transport_equipment.equipment_reference = :equipmentReference"
            + " AND (transport.load_transport_call_id = :transportCallID OR transport.discharge_transport_call_id = :transportCallID)")
    Flux<Seal> findSealsForTransportCallIDAndEquipmentReference(String transportCallID, String equipmentReference);

    @Query("SELECT transport_call.* FROM transport_call"
            + " JOIN mode_of_transport ON (mode_of_transport.mode_of_transport_code = transport_call.mode_of_transport)"
            + " JOIN voyage import_voyage ON (transport_call.import_voyage_id = import_voyage.id)"
            + " JOIN voyage export_voyage ON (transport_call.export_voyage_id = export_voyage.id)"
            + " JOIN service ON (service.id = export_voyage.service_id)"
            + " JOIN vessel ON (vessel.id = transport_call.vessel_id)"
            + " LEFT JOIN facility ON (facility.id = transport_call.facility_id)"
            + " LEFT JOIN location ON (location.id = transport_call.location_id)"
            + " WHERE vessel.vessel_imo_number = :vesselIMONumber"
            + " AND mode_of_transport.dcsa_transport_type = :modeOfTransport"
            + " AND (facility.un_location_code = :UNLocationCode"
            + "      OR location.un_location_code = :UNLocationCode)"
            + " AND import_voyage.carrier_voyage_number = :importVoyageNumber"
            + " AND export_voyage.carrier_voyage_number = :exportVoyageNumber"
            + " AND service.carrier_service_code = :carrierServiceCode"
            + " AND (:transportCallSequenceNumber IS NULL OR transport_call.transport_call_sequence_number = :transportCallSequenceNumber)"
    )
    Flux<TransportCall> getTransportCall(String UNLocationCode, String modeOfTransport,
                                         String vesselIMONumber, String carrierServiceCode,
                                         String importVoyageNumber, String exportVoyageNumber,
                                         Integer transportCallSequenceNumber);

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
  Flux<String> findCarrierVoyageNumbersByTransportCallID(String transportCallID);

  @Query(
      "SELECT DISTINCT service.carrier_service_code FROM service"
          + " JOIN voyage voyage ON voyage.service_id = service.id"
          + " JOIN transport_call ON transport_call.import_voyage_id = voyage.id OR transport_call.export_voyage_id = voyage.id"
          + " WHERE transport_call.id = :transportCallID")
  Flux<String> findCarrierServiceCodesByTransportCallID(String transportCallID);

  @Query(
      "SELECT DISTINCT tc.id FROM transport_call tc "
          + "JOIN transport t "
          + "ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + "ON st.transport_id = t.id "
          + "JOIN shipment s "
          + "ON s.id = st.shipment_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findTransportCallIDByCarrierBookingRef(String carrierBookingRef);

  @Query(
      "SELECT DISTINCT tc.id FROM transport_call tc "
          + "JOIN transport t "
          + "ON t.load_transport_call_id = tc.id "
          + "JOIN shipment_transport st "
          + "ON st.transport_id = t.id "
          + "JOIN shipment s "
          + "ON s.id = st.shipment_id "
          + "WHERE s.carrier_booking_reference = :carrierBookingRef")
  Flux<String> findTransportCallIDByShippingInstructionReference(String shippingInstructionReference);

  @Query(
      "SELECT DISTINCT tc.id FROM transport_call tc "
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
  Flux<String> findTransportCallIDByTransportDocumentRef(String transportDocumentRef);
}
