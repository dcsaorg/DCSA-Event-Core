package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentPartyService {

	Mono<Optional<List<DocumentPartyTO>>> createDocumentPartiesByBookingID(UUID bookingID, List<DocumentPartyTO> documentParties);
	Mono<Optional<List<DocumentPartyTO>>> createDocumentPartiesByShippingInstructionID(String shippingInstructionID, List<DocumentPartyTO> documentParties);

}
