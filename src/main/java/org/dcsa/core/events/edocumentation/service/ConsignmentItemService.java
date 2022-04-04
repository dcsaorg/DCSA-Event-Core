package org.dcsa.core.events.edocumentation.service;

import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.dcsa.core.events.model.transferobjects.UtilizedTransportEquipmentTO;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ConsignmentItemService {
  Mono<List<ConsignmentItemTO>> createConsignmentItemsByShippingInstructionReferenceAndTOs(
      String shippingInstructionReference,
      List<ConsignmentItemTO> consignmentItemTOs,
      List<UtilizedTransportEquipmentTO> utilizedTransportEquipmentTOs);

  Mono<String> removeConsignmentItemsByShippingInstructionReference(
      String shippingInstructionReference);
}
