package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Equipment;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface EquipmentRepository extends ExtendedRepository<Equipment, String> {
    Mono<Equipment> findByEquipmentReference(String equipmentReference);
}
