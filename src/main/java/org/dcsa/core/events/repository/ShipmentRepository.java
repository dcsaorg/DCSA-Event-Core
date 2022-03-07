package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Shipment;
import org.dcsa.core.events.model.enums.ShipmentEventTypeCode;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface ShipmentRepository
    extends ReactiveCrudRepository<Shipment, UUID>, ShipmentCustomRepository {

  Mono<Shipment> findByCarrierBookingReference(String carrierBookingReference);

  @Query(
      "SELECT COUNT(s.id) "
          + "FROM shipment s "
          + "JOIN booking b ON s.booking_id = b.id "
          + "WHERE (:documentStatus is null OR b.document_status = :documentStatus)")
  Mono<Long> countShipmentsByDocumentStatus(ShipmentEventTypeCode documentStatus);

  @Query(
      "SELECT DISTINCT s.* FROM shipment s "
          + "JOIN shipment_equipment se ON se.shipment_id = s.id "
          + "JOIN cargo_item ci ON ci.shipment_equipment_id = se.id  "
          + "JOIN shipping_instruction si ON si.id = ci.shipping_instruction_id "
          + "WHERE si.id = :shippingInstructionReference")
  Flux<Shipment> findByShippingInstructionReference(String shippingInstructionReference);
}
