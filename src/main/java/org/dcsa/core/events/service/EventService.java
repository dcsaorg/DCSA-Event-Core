package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.service.ExtendedBaseService;

import java.util.UUID;

public interface EventService<T extends Event> extends ExtendedBaseService<T, UUID> {
}
