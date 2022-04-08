package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.RequestedEquipmentEquipment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
public interface RequestedEquipmentEquipmentRepository
    extends ReactiveCrudRepository<RequestedEquipmentEquipment, UUID> {
  Flux<RequestedEquipmentEquipment> findAllByRequestedEquipmentId(UUID requestedEquipmentId);
}
