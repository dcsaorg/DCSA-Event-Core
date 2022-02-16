package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentTO;
import reactor.core.publisher.Flux;

public interface ShipmentService {
  Flux<ShipmentTO> findByShippingInstructionID(String id);
}
