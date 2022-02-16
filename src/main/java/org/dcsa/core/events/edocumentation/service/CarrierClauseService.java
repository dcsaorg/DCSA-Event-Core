package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.CarrierClauseTO;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface CarrierClauseService {
    Flux<CarrierClauseTO> fetchCarrierClausesByTransportDocumentReference(String transportDocumentReference);

    //ToDo method already created to be used in BookingService when refactoring Booking
    Flux<CarrierClauseTO> fetchCarrierClausesByShipmentID(UUID shipmentID);
}
