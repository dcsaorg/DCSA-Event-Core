package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.TransportEvent;
import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.repository.InsertAddonRepository;

import java.util.UUID;

public interface TransportEventRepository extends ExtendedRepository<TransportEvent, UUID>, InsertAddonRepository<TransportEvent> {

}
