package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.exception.CreateException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class ReferenceServiceImpl implements ReferenceService {

  enum ImplementationType {
    BOOKING,
    SHIPPING_INSTRUCTION
  }

  private final ReferenceRepository referenceRepository;

  @Override
  public Mono<Optional<List<ReferenceTO>>> createReferencesByBookingIDAndTOs(
      UUID bookingID, List<ReferenceTO> references) {
    if (bookingID == null) return Mono.error(new CreateException("BookingID cannot be null"));
    return this.createReferencesByIDAndRefTOs(bookingID, references, ImplementationType.BOOKING);
  }

  @Override
  public Mono<Optional<List<ReferenceTO>>> createReferencesByShippingInstructionIDAndTOs(
      String shippingInstructionID, List<ReferenceTO> references) {
    if (shippingInstructionID == null)
      return Mono.error(new CreateException("ShippingInstructionID cannot be null"));
    return this.createReferencesByIDAndRefTOs(
        shippingInstructionID, references, ImplementationType.SHIPPING_INSTRUCTION);
  }

  @Override
  public Mono<Optional<List<ReferenceTO>>> findByBookingID(UUID bookingID) {
    return referenceRepository
        .findByBookingID(bookingID)
        .map(transformRefToRefTO)
        .collectList()
        .map(Optional::of);
  }

  @Override
  public Mono<Optional<List<ReferenceTO>>> findByShippingInstructionID(
      String shippingInstructionID) {
    return referenceRepository
        .findByShippingInstructionID(shippingInstructionID)
        .map(transformRefToRefTO)
        .collectList()
        .map(Optional::of);
  }

  @Override
  public Mono<Optional<List<ReferenceTO>>> resolveReferencesForShippingInstructionID(
      List<ReferenceTO> references, String shippingInstructionID) {

    return referenceRepository
        .deleteByShippingInstructionID(shippingInstructionID)
        .then(
            createReferencesByIDAndRefTOs(
                shippingInstructionID, references, ImplementationType.SHIPPING_INSTRUCTION));
  }

  @Override
  public Mono<Optional<List<ReferenceTO>>> resolveReferencesForBookingID(
      List<ReferenceTO> references, UUID bookingID) {

    return referenceRepository
        .deleteByBookingID(bookingID)
        .then(createReferencesByIDAndRefTOs(bookingID, references, ImplementationType.BOOKING));
  }

  private final Function<Reference, ReferenceTO> transformRefToRefTO =
      ref -> {
        ReferenceTO referenceTO = new ReferenceTO();
        referenceTO.setReferenceType(ref.getReferenceType());
        referenceTO.setReferenceValue(ref.getReferenceValue());
        return referenceTO;
      };

  private Mono<Optional<List<ReferenceTO>>> createReferencesByIDAndRefTOs(
      Object id, List<ReferenceTO> references, ImplementationType impType) {

    if (Objects.isNull(references) || references.isEmpty()) {
      return Mono.just(Optional.of(Collections.emptyList()));
    }

    Stream<Reference> referenceStream =
        references.stream()
            .map(
                r -> {
                  Reference reference = new Reference();
                  if (impType == ImplementationType.BOOKING) {
                    reference.setBookingID((UUID) id);
                  } else if (impType == ImplementationType.SHIPPING_INSTRUCTION) {
                    reference.setShippingInstructionID((String) id);
                  }
                  reference.setReferenceType(r.getReferenceType());
                  reference.setReferenceValue(r.getReferenceValue());
                  return reference;
                });

    return referenceRepository
        .saveAll(Flux.fromStream(referenceStream))
        .map(transformRefToRefTO)
        .collectList()
        .map(Optional::of);
  }
}
