package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Location;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.function.Function;

public interface LocationService extends ExtendedBaseService<Location, String> {

    Mono<LocationTO> ensureResolvable(LocationTO locationTO);

    Mono<LocationTO> findPaymentLocationByShippingInstructionID(String shippingInstructionID);

    Mono<LocationTO> findTOById(String locationID);

    Mono<Optional<LocationTO>> fetchLocationByID(String id);

    Mono<Optional<LocationTO>> createLocationByTO(LocationTO locationTO, Function<String, Mono<Boolean>> updateBookingCallback);

    Mono<Optional<LocationTO>> resolveLocationByTO(String currentLocationIDInBooking, LocationTO locationTO, Function<String, Mono<Boolean>> updateBookingCallback);
}
