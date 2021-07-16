package org.dcsa.core.events.repository;

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

    @Query("SELECT DISTINCT shipment.carrier_booking_reference from dcsa_im_v3_0.shipment shipment"
            + " JOIN dcsa_im_v3_0.shipment_transport shipment_transport ON shipment_transport.shipment_id = shipment.id"
            + " JOIN dcsa_im_v3_0.transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE transport.load_transport_call_id = :transportCallID"
            + " OR transport.discharge_transport_call_id = :transportCallID")
    Flux<String> findBookingReferencesByTransportCallID(String transportCallID);

    @Query("SELECT transport_call.*" +
    " FROM facility, transport, transport_call" +
    " WHERE un_location_code = :unLocationCode" +
    " AND facility.facility_smdg_code = :smdgCode" +
    " AND transport_call.facility_id = facility.id" +
    " AND transport.mode_of_transport = (SELECT mode_of_transport_code FROM dcsa_im_v3_0.mode_of_transport WHERE mode_of_transport.dcsa_transport_type = :modeOfTransport)" +
    " AND transport.vessel_imo_number = :vesselIMONumber" +
    " AND (transport.load_transport_call_id = transport_call.id OR transport_call.id = transport.discharge_transport_call_id)"
    )
    Mono<TransportCall> getTransportCall(String unLocationCode, String smdgCode, String modeOfTransport, String vesselIMONumber);

    @Query("SELECT DISTINCT transport_document.transport_document_reference from dcsa_im_v3_0.transport_document transport_document"
            + " JOIN dcsa_im_v3_0.cargo_item cargo_item ON cargo_item.shipping_instruction_id = transport_document.shipping_instruction_id"
            + " JOIN dcsa_im_v3_0.shipment_equipment shipment_equipment ON shipment_equipment.id = cargo_item.shipment_equipment_id"
            + " JOIN dcsa_im_v3_0.shipment_transport shipment_transport ON shipment_transport.shipment_id = shipment_equipment.shipment_id"
            + " JOIN dcsa_im_v3_0.transport transport ON transport.id = shipment_transport.transport_id"
            + " WHERE transport.load_transport_call_id = :transportCallID"
            + " OR transport.discharge_transport_call_id = :transportCallID")
    Flux<String> findTransportDocumentReferencesByTransportCallID(String transportCallID);
}
