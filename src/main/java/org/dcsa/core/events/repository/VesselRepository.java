package org.dcsa.core.events.repository;

import org.dcsa.core.repository.ExtendedRepository;
import org.dcsa.core.repository.InsertAddonRepository;
import org.dcsa.core.events.model.Vessel;

public interface VesselRepository extends ExtendedRepository<Vessel, String>, InsertAddonRepository<Vessel> {
}
