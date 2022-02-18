package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ShipmentService {
  Mono<List<ShipmentTO>> findByShippingInstructionID(String id);
}
