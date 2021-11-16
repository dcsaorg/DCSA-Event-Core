package org.dcsa.core.events.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.repository.InsertAddonRepository;
import org.dcsa.core.events.model.Vessel;

import java.util.UUID;

public interface VesselRepository extends ExtendedRepository<Vessel, UUID> {
}
