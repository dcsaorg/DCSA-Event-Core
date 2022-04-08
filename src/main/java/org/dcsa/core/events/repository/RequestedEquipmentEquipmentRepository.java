package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.RequestedEquipmentEquipment;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RequestedEquipmentEquipmentRepository
    extends ReactiveCrudRepository<RequestedEquipmentEquipment, UUID> {
  Flux<RequestedEquipmentEquipment> findAllByRequestedEquipmentId(UUID requestedEquipmentId);

  @Modifying
  @Query(
      "DELETE FROM requested_equipment_equipment "
          + "WHERE requested_equipment_id = (SELECT ree.requested_equipment_id FROM booking b "
          + "JOIN requested_equipment re ON b.id = re.booking_id "
          + "JOIN requested_equipment_equipment ree ON re.id = ree.requested_equipment_id "
          + "WHERE b.id = :bookingId)")
  Mono<Void> deleteByBookingId(UUID bookingId);
}
