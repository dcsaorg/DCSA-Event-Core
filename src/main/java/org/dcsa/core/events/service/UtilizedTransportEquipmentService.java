package org.dcsa.core.events.service;

import org.dcsa.core.events.model.transferobjects.ShippingInstructionTO;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface UtilizedTransportEquipmentService {
  Mono<List<UtilizedTransportEquipmentTO>> createUtilizedTransportEquipment(
      UUID shipmentID,
      String shippingInstructionReference,
      List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs);

  Mono<List<UtilizedTransportEquipmentTO>> findUtilizedTransportEquipmentByShipmentID(
      UUID shipmentID);

  Mono<List<UtilizedTransportEquipmentTO>> addUtilizedTransportEquipmentToShippingInstruction(
      List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs,
      ShippingInstructionTO shippingInstructionTO);
}
