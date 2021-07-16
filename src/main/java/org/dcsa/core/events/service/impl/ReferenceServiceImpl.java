package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ReferenceServiceImpl extends ExtendedBaseServiceImpl<ReferenceRepository, Reference, UUID> implements ReferenceService {
  private final ReferenceRepository referenceRepository;

  @Override
  public ReferenceRepository getRepository() {
    return referenceRepository;
  }

  @Override
  public Flux<Reference> findByShippingInstructionID(String shippingInstructionID) {
    return referenceRepository.findByShippingInstructionID(shippingInstructionID);
  }

  @Override
  public Flux<Reference> findByShipmentID(UUID shipmentID) {
    return referenceRepository.findByShipmentID(shipmentID);
  }

  @Override
  public Flux<Reference> findByTransportDocumentReference(String transportDocumentReference) {
    return referenceRepository.findByTransportDocumentReference(transportDocumentReference);
  }

}
