package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.VesselPosition;
import org.dcsa.core.events.model.transferobjects.VesselPositionTO;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VesselPositionRepository extends ExtendedRepository<VesselPosition, String> {
    Mono<VesselPosition> findByLatitudeAndLongitude(
            String latitude,
            String longitude
    );

    default Mono<VesselPosition> findByContent(VesselPositionTO vesselPositionTO) {
        return findByLatitudeAndLongitude(
                vesselPositionTO.getLatitude(),
                vesselPositionTO.getLongitude()
        );
    }

}
