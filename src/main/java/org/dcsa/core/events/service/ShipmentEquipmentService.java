package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.ShipmentEquipmentTO;
import org.dcsa.core.events.model.transferobjects.ShippingInstructionTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ShipmentEquipmentService {
	Mono<List<ShipmentEquipmentTO>> createShipmentEquipment(UUID shipmentID, String shippingInstructionID, List<ShipmentEquipmentTO> shipmentEquipments);

	Mono<List<ShipmentEquipmentTO>> findShipmentEquipmentByShipmentID(UUID shipmentID);

	Mono<List<ShipmentEquipmentTO>> insertShipmentEquipmentTOs(List<ShipmentEquipmentTO> shipmentEquipments, ShippingInstructionTO shippingInstructionTO);

	Mono<List<ShipmentEquipmentTO>> resolveShipmentEquipmentsForShippingInstructionID(List<ShipmentEquipmentTO> shipmentEquipments, ShippingInstructionTO shippingInstructionTO);
}
