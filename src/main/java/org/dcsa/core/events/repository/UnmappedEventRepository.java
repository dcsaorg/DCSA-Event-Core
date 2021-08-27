package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.UnmappedEvent;
import org.dcsa.core.repository.ExtendedRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UnmappedEventRepository extends ExtendedRepository<UnmappedEvent, UUID> {}
