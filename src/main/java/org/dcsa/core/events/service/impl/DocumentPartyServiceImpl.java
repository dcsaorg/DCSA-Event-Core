package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.DisplayedAddress;
import org.dcsa.core.events.model.DocumentParty;
import org.dcsa.core.events.model.enums.ColumnReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import org.dcsa.core.events.repository.DisplayedAddressRepository;
import org.dcsa.core.events.repository.DocumentPartyRepository;
import org.dcsa.core.events.service.DocumentPartyService;
import org.dcsa.skernel.service.PartyService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentPartyServiceImpl implements DocumentPartyService {

  private final DocumentPartyRepository documentPartyRepository;
  private final DisplayedAddressRepository displayedAddressRepository;
  private final PartyService partyService;

  @Override
  public Mono<List<DocumentPartyTO>> createDocumentPartiesByBookingID(
      UUID bookingID, List<DocumentPartyTO> documentParties) {
    return createDocumentParties(bookingID, ColumnReferenceType.BOOKING, documentParties);
  }

  @Override
  public Mono<List<DocumentPartyTO>> createDocumentPartiesByShippingInstructionID(
      UUID shippingInstructionReference, List<DocumentPartyTO> documentParties) {
    return createDocumentParties(
        shippingInstructionReference, ColumnReferenceType.SHIPPING_INSTRUCTION, documentParties);
  }

  @Override
  public Mono<List<DocumentPartyTO>> fetchDocumentPartiesByBookingID(UUID bookingID) {
    return this.fetchDocumentPartiesByID(bookingID, ColumnReferenceType.BOOKING);
  }

  @Override
  public Mono<List<DocumentPartyTO>> fetchDocumentPartiesByByShippingInstructionID(
      UUID shippingInstructionID) {
    return this.fetchDocumentPartiesByID(
        shippingInstructionID, ColumnReferenceType.SHIPPING_INSTRUCTION);
  }

  @Override
  public Mono<List<DocumentPartyTO>> resolveDocumentPartiesForShippingInstructionID(
      UUID shippingInstructionID, List<DocumentPartyTO> documentPartyTOs) {
    // this will create orphan parties
    return documentPartyRepository
        .findByShippingInstructionID(shippingInstructionID)
        .flatMap(
            documentParty ->
                displayedAddressRepository
                    .deleteAllByDocumentPartyID(documentParty.getId())
                    .thenReturn(documentParty))
        .flatMap(
            ignored -> documentPartyRepository.deleteByShippingInstructionID(shippingInstructionID))
        .then(
            createDocumentPartiesByShippingInstructionID(shippingInstructionID, documentPartyTOs));
  }

  Mono<List<DocumentPartyTO>> fetchDocumentPartiesByID(Object id, ColumnReferenceType implType) {

    if (id == null) return Mono.empty();

    Flux<DocumentParty> documentParties =
        Flux.error(new RuntimeException("Invalid implementationType provided"));

    if (implType == ColumnReferenceType.BOOKING) {
      documentParties = documentPartyRepository.findByBookingID((UUID) id);
    } else if (implType == ColumnReferenceType.SHIPPING_INSTRUCTION) {
      documentParties = documentPartyRepository.findByShippingInstructionID((UUID) id);
    }

    return documentParties
        .flatMap(
            dp -> {
              DocumentPartyTO documentPartyTO = new DocumentPartyTO();
              documentPartyTO.setPartyFunction(dp.getPartyFunction());
              documentPartyTO.setIsToBeNotified(dp.getIsToBeNotified());

              return Mono.when(
                      partyService.findTOById(dp.getPartyID()).doOnNext(documentPartyTO::setParty),
                      fetchDisplayAddressByDocumentID(dp.getId())
                          .doOnNext(documentPartyTO::setDisplayedAddress))
                  .thenReturn(documentPartyTO);
            })
        .collectList();
  }

  Mono<List<String>> fetchDisplayAddressByDocumentID(UUID documentPartyID) {
    return displayedAddressRepository
        .findByDocumentPartyIDOrderByAddressLineNumber(documentPartyID)
        .map(DisplayedAddress::getAddressLine)
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  private Mono<List<DisplayedAddress>> createDisplayedAddress(
      List<String> displayedAddresses, UUID documentPartyId) {
    if (displayedAddresses == null || displayedAddresses.isEmpty()) {
      return Mono.empty();
    }
    return Flux.fromIterable(displayedAddresses)
        .map(
            s -> {
              DisplayedAddress displayedAddress = new DisplayedAddress();
              displayedAddress.setDocumentPartyID(documentPartyId);
              displayedAddress.setAddressLine(s);
              displayedAddress.setAddressLineNumber(displayedAddresses.indexOf(s));
              return displayedAddress;
            })
        .collectList();
  }

  private Mono<List<DocumentPartyTO>> createDocumentParties(
      UUID id, ColumnReferenceType refColumn, List<DocumentPartyTO> documentParties) {
    if (Objects.isNull(documentParties) || documentParties.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }

    return Flux.fromIterable(documentParties)
        .flatMap(
            documentPartyTO ->
                partyService
                    .createPartyByTO(documentPartyTO.getParty())
                    .doOnNext(documentPartyTO::setParty)
                    .map(
                        partyTO ->
                            getDocumentPartyByRefColumn(
                                id, refColumn, documentPartyTO.getParty().getId(), documentPartyTO))
                    .flatMap(documentPartyRepository::save)
                    .flatMap(
                        documentParty ->
                            createDisplayedAddress(
                                documentPartyTO.getDisplayedAddress(), documentParty.getId()))
                    .flatMap(displayedAddresses -> displayedAddressRepository.saveAll(displayedAddresses)
                      .collectList())
                    .thenReturn(documentPartyTO))
        .collectList();
  }

  private Mono<DocumentParty> createDocumentPartyFromPartyTo(UUID id, ColumnReferenceType refColumn, DocumentPartyTO documentPartyTO) {
    return Mono.justOrEmpty(documentPartyTO).map(
      partyTO ->
        getDocumentPartyByRefColumn(
          id, refColumn, documentPartyTO.getParty().getId(), documentPartyTO)) //ToDO partyID is not set on documentParty
      .flatMap(documentPartyRepository::save);
  }

  private DocumentParty getDocumentPartyByRefColumn(
      UUID id,
      ColumnReferenceType columnReferenceType,
      UUID partyID,
      DocumentPartyTO documentPartyTO) {
    DocumentParty documentParty = new DocumentParty();
    documentParty.setPartyID(partyID);
    documentParty.setPartyFunction(documentPartyTO.getPartyFunction());
    documentParty.setIsToBeNotified(documentPartyTO.getIsToBeNotified());

    switch (columnReferenceType) {
      case BOOKING -> documentParty.setBookingID(id);
      case SHIPPING_INSTRUCTION -> documentParty.setShippingInstructionID(id);
      case SHIPMENT -> documentParty.setShipmentID(id);
      default -> {
        throw new AssertionError("This should not happen: Missing switch case for: " + columnReferenceType);
      }
    }

    return documentParty;
  }
}
