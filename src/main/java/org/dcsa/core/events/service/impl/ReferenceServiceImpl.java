package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.service.ReferenceService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.util.MappingUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class ReferenceServiceImpl implements ReferenceService {

  enum ImplementationType {
    BOOKING,
    SHIPPING_INSTRUCTION
  }

  private final ReferenceRepository referenceRepository;

  @Override
  public Mono<List<ReferenceTO>> createReferencesByBookingIDAndTOs(
      UUID bookingID, List<ReferenceTO> references) {
    if (bookingID == null) return Mono.error(new CreateException("BookingID cannot be null"));
    return this.createReferencesByIDAndRefTOs(bookingID, references, ImplementationType.BOOKING);
  }

  @Override
  public Mono<List<ReferenceTO>> createReferencesByShippingInstructionIdAndTOs(
      UUID shippingInstructionID, List<ReferenceTO> references) {
    if (shippingInstructionID == null)
      return Mono.error(new CreateException("ShippingInstructionReference cannot be null"));
    return this.createReferencesByIDAndRefTOs(
        shippingInstructionID, references, ImplementationType.SHIPPING_INSTRUCTION);
  }

  @Override
  public Mono<List<ReferenceTO>>
      createReferencesByShippingInstructionReferenceAndConsignmentIdAndTOs(
          UUID shippingInstructionReference, UUID consignmentId, List<ReferenceTO> references) {
    if (shippingInstructionReference == null)
      return Mono.error(new CreateException("ShippingInstructionReference cannot be null"));
    return this.createReferencesByIDAndRefTOs(
        shippingInstructionReference,
        references,
        ImplementationType.SHIPPING_INSTRUCTION,
        consignmentId);
  }

  @Override
  public Mono<List<ReferenceTO>> findByBookingID(UUID bookingID) {
    return referenceRepository.findByBookingID(bookingID).map(transformRefToRefTO).collectList();
  }

  @Override
  public Mono<List<ReferenceTO>> findByShippingInstructionID(UUID shippingInstructionID) {
    return referenceRepository
        .findByShippingInstructionID(shippingInstructionID)
        .map(transformRefToRefTO)
        .collectList();
  }

  @Override
  public Mono<List<ReferenceTO>> resolveReferencesForShippingInstructionReference(
      List<ReferenceTO> references, UUID shippingInstructionReference) {

    return referenceRepository
        .deleteByShippingInstructionID(shippingInstructionReference)
        .then(
            createReferencesByIDAndRefTOs(
                shippingInstructionReference, references, ImplementationType.SHIPPING_INSTRUCTION));
  }

  @Override
  public Mono<List<ReferenceTO>> resolveReferencesForBookingID(
      List<ReferenceTO> references, UUID bookingID) {

    return referenceRepository
        .deleteByBookingID(bookingID)
        .then(createReferencesByIDAndRefTOs(bookingID, references, ImplementationType.BOOKING));
  }

  private Mono<List<ReferenceTO>> createReferencesByIDAndRefTOs(
      Object id, List<ReferenceTO> references, ImplementationType impType) {
    return createReferencesByIDAndRefTOs(id, references, impType, null);
  }

  private Mono<List<ReferenceTO>> createReferencesByIDAndRefTOs(
      Object id, List<ReferenceTO> references, ImplementationType impType, UUID consignmentId) {

    if (Objects.isNull(references) || references.isEmpty()) {
      return Mono.empty();
    }

    return Flux.fromIterable(references)
        .map(
            r -> {
              Reference reference = new Reference();
              if (impType == ImplementationType.BOOKING) {
                reference.setBookingID((UUID) id);
              } else if (impType == ImplementationType.SHIPPING_INSTRUCTION) {
                reference.setShippingInstructionID((UUID) id);
              }
              if (consignmentId != null) reference.setConsignmentItemID(consignmentId);
              reference.setReferenceType(r.getReferenceType());
              reference.setReferenceValue(r.getReferenceValue());
              return reference;
            })
        .buffer(MappingUtils.SQL_LIST_BUFFER_SIZE) // process in smaller batches
        .concatMap(referenceRepository::saveAll)
        .map(transformRefToRefTO)
        .collectList();
  }

  private final Function<Reference, ReferenceTO> transformRefToRefTO =
      ref -> {
        ReferenceTO referenceTO = new ReferenceTO();
        referenceTO.setReferenceType(ref.getReferenceType());
        referenceTO.setReferenceValue(ref.getReferenceValue());
        return referenceTO;
      };
}
