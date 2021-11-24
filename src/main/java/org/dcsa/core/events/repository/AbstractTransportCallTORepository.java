package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.repository.ExtendedRepository;

public interface AbstractTransportCallTORepository<T extends TransportCallTO> extends ExtendedRepository<T, String> {
}
