package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import org.dcsa.core.events.model.transferobjects.ShippingInstructionTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UtilizedTransportEquipmentService {
	Mono<List<UtilizedTransportEquipmentTO>> createUtilizedTransportEquipment(String shippingInstructionReference, List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs);

	Mono<List<UtilizedTransportEquipmentTO>> findUtilizedTransportEquipmentByShipmentID(UUID shipmentID);

	Mono<List<UtilizedTransportEquipmentTO>> addUtilizedTransportEquipmentToShippingInstruction(List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs, ShippingInstructionTO shippingInstructionTO);

	Mono<List<UtilizedTransportEquipmentTO>> resolveUtilizedTransportEquipmentsForShippingInstructionReference(List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs, ShippingInstructionTO shippingInstructionTO);
}
