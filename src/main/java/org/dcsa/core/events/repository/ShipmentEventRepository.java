package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.ShipmentEvent;
import org.dcsa.core.repository.ExtendedRepository;

import java.util.UUID;

public interface ShipmentEventRepository extends ExtendedRepository<ShipmentEvent, UUID> {}
