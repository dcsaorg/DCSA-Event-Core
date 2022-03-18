package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Facility;
import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LocationRepository extends ReactiveCrudRepository<Location, String> {
    @Query("SELECT location.*"
            + "  FROM location"
            + "  JOIN shipping_instruction ON (location.id=shipping_instruction.invoice_payable_at)"
            + " WHERE shipping_instruction.id = :shippingInstructionReference"
    )
    Mono<Location> findPaymentLocationByShippingInstructionReference(String shippingInstructionReference);

    Mono<Location> findByAddressIDAndFacilityIDAndLocationNameAndLatitudeAndLongitudeAndUnLocationCode(
            UUID addressID,
            UUID facilityID,
            String locationName,
            String latitude,
            String longitude,
            String unLocationCode
    );

    default Mono<Location> findByContent(LocationTO locationTO) {
        Address address = locationTO.getAddress();
        Facility facility = locationTO.getFacility();
        UUID addressID = address != null ? address.getId() : null;
        UUID facilityID = facility != null ? facility.getFacilityID() : null;
        return findByAddressIDAndFacilityIDAndLocationNameAndLatitudeAndLongitudeAndUnLocationCode(
                addressID,
                facilityID,
                locationTO.getLocationName(),
                locationTO.getLatitude(),
                locationTO.getLongitude(),
                locationTO.getUnLocationCode()
        );
    }
}
