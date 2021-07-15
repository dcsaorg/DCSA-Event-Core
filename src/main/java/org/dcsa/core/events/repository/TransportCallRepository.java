package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Mono;

public interface TransportCallRepository extends ExtendedRepository<TransportCall, String> {

//    @Query("SELECT transport_call FROM transport_call WHERE transport_call.id = :transportCallId AND transport_call.facility_id = :facilityId")


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
}
