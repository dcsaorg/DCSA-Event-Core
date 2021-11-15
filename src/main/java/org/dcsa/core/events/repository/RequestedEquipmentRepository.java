package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.RequestedEquipment;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface RequestedEquipmentRepository extends ExtendedRepository<RequestedEquipment, UUID> {
  Flux<RequestedEquipment> findByBookingID(UUID bookingID);
}
