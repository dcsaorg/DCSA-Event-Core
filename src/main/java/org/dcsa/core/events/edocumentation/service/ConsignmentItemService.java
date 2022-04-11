package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface ConsignmentItemService {

  Mono<List<ConsignmentItemTO>> fetchConsignmentItemsTOByShippingInstructionID(
      UUID shippingInstructionID);

  Mono<List<ConsignmentItemTO>> createConsignmentItemsByShippingInstructionIDAndTOs(
      UUID shippingInstructionID,
      List<ConsignmentItemTO> consignmentItemTOs,
      List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs);

  Mono<Void> removeConsignmentItemsByShippingInstructionID(UUID shippingInstructionID);
}
