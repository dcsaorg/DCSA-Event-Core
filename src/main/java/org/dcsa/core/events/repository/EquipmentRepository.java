package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Equipment;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EquipmentRepository extends ReactiveCrudRepository<Equipment, String> {
    Mono<Equipment> findByEquipmentReference(String equipmentReference);
}
