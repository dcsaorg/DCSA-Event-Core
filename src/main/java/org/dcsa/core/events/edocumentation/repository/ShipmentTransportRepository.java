package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.model.ShipmentTransport;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface ShipmentTransportRepository extends ReactiveCrudRepository<ShipmentTransport, UUID> {

  Flux<ShipmentTransport> findAllByShipmentID(UUID shipmentId);

  @Query("""
    select st.*
    from shipment_transport st
    join shipment s on s.id = st.shipment_id
    where s.carrier_booking_reference = :carrierBookingReference
    """)
  Flux<ShipmentTransport> findByCarrierBookingReference(String carrierBookingReference);
}
