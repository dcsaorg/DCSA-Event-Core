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
            + " JOIN cargo_item cargo_item ON cargo_item.shipping_instruction_id = transport_document.shipping_instruction_id"
            + " JOIN shipment_equipment shipment_equipment ON shipment_equipment.id = cargo_item.shipment_equipment_id"
            + " JOIN shipment_transport shipment_transport ON shipment_transport.shipment_id = shipment_equipment.shipment_id"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE transport.load_transport_call_id = :transportCallID"
            + " OR transport.discharge_transport_call_id = :transportCallID")
    Flux<String> findTransportDocumentReferencesByTransportCallID(String transportCallID);

    @Query("SELECT DISTINCT seal.* FROM seal"
            + " JOIN shipment_equipment shipment_equipment ON shipment_equipment.id = seal.shipment_equipment_id"
            + " JOIN shipment_transport shipment_transport ON shipment_transport.shipment_id = shipment_equipment.shipment_id"
            + " JOIN transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE shipment_equipment.equipment_reference = :equipmentReference"
            + " AND (transport.load_transport_call_id = :transportCallID OR transport.discharge_transport_call_id = :transportCallID)")
    Flux<Seal> findSealsForTransportCallIDAndEquipmentReference(String transportCallID, String equipmentReference);

    @Query("SELECT transport_call.* FROM transport_call"
            + " JOIN facility ON (facility.id = transport_call.facility_id)"
            + " JOIN transport ON (transport.load_transport_call_id = transport_call.id OR transport.discharge_transport_call_id = transport_call.id)"
            + " JOIN mode_of_transport ON (mode_of_transport.mode_of_transport_code = transport.mode_of_transport)"
            + " WHERE transport.vessel_imo_number = :vesselIMONumber"
            + " AND mode_of_transport.dcsa_transport_type = :modeOfTransport"
            + " AND facility.facility_smdg_code = :facilitySMDGCode"
            + " AND facility.un_location_code = :UNLocationCode")
    Mono<TransportCall> getTransportCall(String UNLocationCode, String facilitySMDGCode, String modeOfTransport, String vesselIMONumber);
}
