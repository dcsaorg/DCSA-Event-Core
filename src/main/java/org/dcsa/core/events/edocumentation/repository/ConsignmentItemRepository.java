package org.dcsa.core.events.edocumentation.repository;

import org.dcsa.core.events.edocumentation.model.ConsignmentItem;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ConsignmentItemRepository extends ReactiveCrudRepository<ConsignmentItem, UUID> {
  // TODO
}
