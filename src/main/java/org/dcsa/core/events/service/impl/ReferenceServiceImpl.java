package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ReferenceServiceImpl
    extends ExtendedBaseServiceImpl<ReferenceRepository, Reference, UUID>
    implements ReferenceService {
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

  @Override
  public Mono<Optional<List<ReferenceTO>>> createReferencesAndTOs(
      UUID bookingID, String shippingInstructionID, List<ReferenceTO> references) {

    if (Objects.isNull(references) || references.isEmpty()) {
      return Mono.just(Optional.of(Collections.emptyList()));
    }

    Stream<Reference> referenceStream =
        references.stream()
            .map(
                r -> {
                  Reference reference = new Reference();
                  if (bookingID != null) {
                    reference.setBookingID(bookingID);
                  }
                  if (shippingInstructionID != null) {
                    reference.setShippingInstructionID(shippingInstructionID);
                  }
                  reference.setReferenceType(r.getReferenceType());
                  reference.setReferenceValue(r.getReferenceValue());
                  return reference;
                });

    return referenceRepository
        .saveAll(Flux.fromStream(referenceStream))
        .map(
            r -> {
              ReferenceTO referenceTO = new ReferenceTO();
              referenceTO.setReferenceType(r.getReferenceType());
              referenceTO.setReferenceValue(r.getReferenceValue());
              return referenceTO;
            })
        .collectList()
        .map(Optional::of);
  }
}
