package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Shipment;
import org.dcsa.core.events.model.enums.DocumentStatus;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentRepository
    extends ExtendedRepository<Shipment, UUID>, ShipmentCustomRepository {

  Mono<Shipment> findByCarrierBookingReference(String carrierBookingReference);

  @Query(
      "SELECT COUNT(s.id) "
          + "FROM shipment s "
          + "JOIN booking b ON s.booking_id = b.id "
          + "WHERE (:documentStatus is null OR b.document_status = :documentStatus)")
  Mono<Long> countShipmentsByDocumentStatus(DocumentStatus documentStatus);
}
