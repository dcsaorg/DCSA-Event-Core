package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TransportCallVoyage;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;

public interface TransportCallVoyageRepository extends ExtendedRepository<TransportCallVoyage, String> {

    @Query("SELECT transport_call_voyage.* FROM transport_call_voyage WHERE transport_call_voyage.transport_call_id = :transportCallID")
    Flux<TransportCallVoyage> findByTransportCallID(String transportCallID);
}
