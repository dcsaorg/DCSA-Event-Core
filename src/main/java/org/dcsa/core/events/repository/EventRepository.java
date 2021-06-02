package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.repository.ExtendedRepository;

import java.util.UUID;

public interface EventRepository extends ExtendedRepository<Event, UUID> {

}
