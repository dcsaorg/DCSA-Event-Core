package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.repository.ExtendedRepository;

import java.util.UUID;

public interface AbstractTransportCallTORepository<T extends TransportCallTO> extends ExtendedRepository<T, UUID> {
}
