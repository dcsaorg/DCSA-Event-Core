package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.ShipmentEquipmentTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ShipmentEquipmentService {
	Mono<List<ShipmentEquipmentTO>> createShipmentEquipment(UUID shipmentID, String shippingInstructionID, List<ShipmentEquipmentTO> shipmentEquipments);

	Mono<List<ShipmentEquipmentTO>> findShipmentEquipmentByShipmentID(UUID shipmentID);
}
