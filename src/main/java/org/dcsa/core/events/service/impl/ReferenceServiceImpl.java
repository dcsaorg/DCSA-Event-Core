package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.exception.CreateException;
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
  public Mono<Optional<List<ReferenceTO>>> createReferencesByBookingIDAndTOs(
      UUID bookingID, List<ReferenceTO> references) {
    if (bookingID == null) return Mono.error(new CreateException("BookingID cannot be null"));
    return createReferencesAndTOs(bookingID, null, references);
  }

  @Override
  public Mono<Optional<List<ReferenceTO>>> createReferencesByShippingInstructionIDAndTOs(
      String shippingInstructionID, List<ReferenceTO> references) {
    if (shippingInstructionID == null)
      return Mono.error(new CreateException("ShippingInstructionID cannot be null"));
    return createReferencesAndTOs(null, shippingInstructionID, references);
  }

  private Mono<Optional<List<ReferenceTO>>> createReferencesAndTOs(
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

  @Override
  public Mono<Optional<List<ReferenceTO>>> resolveReferencesForShippingInstructionID(
      List<ReferenceTO> references, String shippingInstructionID) {

    return referenceRepository
        .deleteByShippingInstructionID(shippingInstructionID)
        .then(createReferencesAndTOs(null, shippingInstructionID, references));
  }

  @Override
  public Mono<Optional<List<ReferenceTO>>> resolveReferencesForBookingID(
      List<ReferenceTO> references, UUID bookingID) {

    return referenceRepository
        .deleteByBookingID(bookingID)
        .then(createReferencesAndTOs(bookingID, null, references));
  }
}
