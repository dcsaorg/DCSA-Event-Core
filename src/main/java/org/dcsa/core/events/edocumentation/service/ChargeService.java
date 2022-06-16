package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.ChargeTO;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface ChargeService {

  Flux<ChargeTO> fetchChargesByTransportDocumentID(UUID transportDocumentID);

  Flux<ChargeTO> fetchChargesByShipmentID(UUID shipmentID);
}
