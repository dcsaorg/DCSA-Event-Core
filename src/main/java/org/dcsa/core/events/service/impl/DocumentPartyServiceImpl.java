package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.ColumnReferenceType;
import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.DocumentPartyService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.dcsa.skernel.model.Party;
import org.dcsa.skernel.model.PartyContactDetails;
import org.dcsa.skernel.model.PartyIdentifyingCode;
import org.dcsa.skernel.model.mapper.PartyMapper;
import org.dcsa.skernel.model.transferobjects.PartyContactDetailsTO;
import org.dcsa.skernel.model.transferobjects.PartyTO;
import org.dcsa.skernel.repositority.PartyContactDetailsRepository;
import org.dcsa.skernel.repositority.PartyIdentifyingCodeRepository;
import org.dcsa.skernel.repositority.PartyRepository;
import org.dcsa.skernel.service.AddressService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DocumentPartyServiceImpl implements DocumentPartyService {

  private final DocumentPartyRepository documentPartyRepository;
  private final DisplayedAddressRepository displayedAddressRepository;
  private final PartyRepository partyRepository;
  private final PartyIdentifyingCodeRepository partyIdentifyingCodeRepository;
  private final PartyContactDetailsRepository partyContactDetailsRepository;

  private final AddressService addressService;

  private final PartyMapper partyMapper;

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
                      fetchPartyByID(dp.getPartyID()).doOnNext(documentPartyTO::setParty),
                      fetchDisplayAddressByDocumentID(dp.getId())
                          .doOnNext(documentPartyTO::setDisplayedAddress))
                  .thenReturn(documentPartyTO);
            })
        .collectList();
  }

  Mono<PartyTO> fetchPartyByID(String partyID) {
    if (partyID == null) return Mono.empty();
    return partyRepository
        .findByIdOrEmpty(partyID)
        .flatMap(
            p -> {
              PartyTO partyTO = partyMapper.partyToDTO(p);

              return Mono.when(
                      addressService
                          .findByIdOrEmpty(p.getAddressID())
                          .doOnNext(partyTO::setAddress),
                      fetchIdentifyingCodesByPartyID(partyID)
                          .doOnNext(partyTO::setIdentifyingCodes),
                      fetchPartyContactDetailsByPartyID(partyID)
                          .doOnNext(partyTO::setPartyContactDetails))
                  .thenReturn(partyTO);
            });
  }

  Mono<List<String>> fetchDisplayAddressByDocumentID(UUID documentPartyID) {
    return displayedAddressRepository
        .findByDocumentPartyIDOrderByAddressLineNumber(documentPartyID)
        .map(DisplayedAddress::getAddressLine)
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  Mono<List<PartyTO.IdentifyingCode>> fetchIdentifyingCodesByPartyID(String partyID) {
    return partyIdentifyingCodeRepository
        .findAllByPartyID(partyID)
        .map(
            idc ->
                PartyTO.IdentifyingCode.builder()
                    .partyCode(idc.getPartyCode())
                    .codeListName(idc.getCodeListName())
                    .dcsaResponsibleAgencyCode(idc.getDcsaResponsibleAgencyCode())
                    .build())
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  Mono<List<PartyContactDetailsTO>> fetchPartyContactDetailsByPartyID(String partyID) {
    return partyContactDetailsRepository
        .findByPartyID(partyID)
        .map(pcd -> new PartyContactDetailsTO(pcd.getName(), pcd.getPhone(), pcd.getEmail()))
        .collectList()
        .flatMap(
            partyContactDetailsTOS -> {
              if (partyContactDetailsTOS.isEmpty()) {
                return Mono.error(
                    ConcreteRequestErrorMessageException.notFound(
                        "No contacts details were found for party"));
              }
              return Mono.just(partyContactDetailsTOS);
            });
  }

  private Mono<List<DocumentPartyTO>> createDocumentParties(
      Object id, ColumnReferenceType refColumn, List<DocumentPartyTO> documentParties) {
    if (Objects.isNull(documentParties) || documentParties.isEmpty()) {
      return Mono.just(Collections.emptyList());
    }

    return Flux.fromIterable(documentParties)
        .flatMap(
            dp ->
                // party is mandatory, cannot be null in document party as per API specs
                createPartyByTO(dp.getParty())
                    .flatMap(
                        t -> {
                          DocumentParty documentParty =
                              getDocumentPartyByRefColumn(id, refColumn, t.getT1(), dp);
                          return documentPartyRepository
                              .save(documentParty)
                              .map(
                                  savedDp -> {
                                    DocumentPartyTO documentPartyTO = new DocumentPartyTO();
                                    documentPartyTO.setParty(t.getT2());
                                    documentPartyTO.setDisplayedAddress(dp.getDisplayedAddress());
                                    documentPartyTO.setPartyFunction(savedDp.getPartyFunction());
                                    documentPartyTO.setIsToBeNotified(savedDp.getIsToBeNotified());
                                    return Tuples.of(savedDp.getId(), documentPartyTO);
                                  });
                        }))
        .flatMap(
            t -> {
              Stream<DisplayedAddress> displayedAddressStream =
                  t.getT2().getDisplayedAddress().stream()
                      .map(
                          da -> {
                            DisplayedAddress displayedAddress = new DisplayedAddress();
                            displayedAddress.setDocumentPartyID(t.getT1());
                            displayedAddress.setAddressLine(da);
                            displayedAddress.setAddressLineNumber(
                                t.getT2().getDisplayedAddress().indexOf(da));
                            return displayedAddress;
                          });

              return displayedAddressRepository
                  .saveAll(Flux.fromStream(displayedAddressStream))
                  .map(DisplayedAddress::getAddressLine)
                  .collectList()
                  .flatMap(
                      s -> {
                        t.getT2().setDisplayedAddress(s);
                        return Mono.just(t.getT2());
                      });
            })
        .collectList();
  }

  private Mono<Tuple2<String, PartyTO>> createPartyByTO(final PartyTO partyTO) {

    Mono<Tuple2<String, PartyTO>> partyMap;

    if (Objects.isNull(partyTO.getAddress())) {

      partyMap =
          partyRepository
              .save(partyMapper.dtoToParty(partyTO))
              .map(
                  p ->
                      Tuples.of(
                          p.getId(),
                          p.toPartyTO(
                              partyTO.getNmftaCode(),
                              partyTO.getAddress(),
                              partyTO.getIdentifyingCodes())));

    } else {
      // if there is an address connected to the party, we need to create it first.
      partyMap =
          addressService
              .ensureResolvable(partyTO.getAddress())
              .flatMap(
                  address -> {
                    Party party = partyMapper.dtoToParty(partyTO);
                    party.setAddressID(address.getId());
                    return partyRepository
                        .save(party)
                        .map(
                            p -> {
                              PartyTO pTO =
                                  p.toPartyTO(
                                      partyTO.getNmftaCode(),
                                      address,
                                      partyTO.getIdentifyingCodes());
                              return Tuples.of(p.getId(), pTO);
                            });
                  });
    }

    return partyMap
        .flatMap(
            t -> {
              Stream<PartyContactDetails> partyContactDetailsStream =
                  partyTO.getPartyContactDetails().stream()
                      .map(pcdTO -> pcdTO.toPartyContactDetails(t.getT1()));

              return partyContactDetailsRepository
                  .saveAll(Flux.fromStream(partyContactDetailsStream))
                  .map(PartyContactDetails::toPartyTO)
                  .collectList()
                  .flatMap(
                      pcds -> {
                        t.getT2().setPartyContactDetails(pcds);
                        return Mono.just(t);
                      });
            })
        .flatMap(
            t -> {
              Stream<PartyIdentifyingCode> partyIdentifyingCodeStream =
                  partyTO.getIdentifyingCodes().stream()
                      .map(
                          idc -> {
                            PartyIdentifyingCode partyIdentifyingCode = new PartyIdentifyingCode();
                            partyIdentifyingCode.setPartyID(t.getT1());
                            partyIdentifyingCode.setDcsaResponsibleAgencyCode(
                                idc.getDcsaResponsibleAgencyCode());
                            partyIdentifyingCode.setCodeListName(idc.getCodeListName());
                            partyIdentifyingCode.setPartyCode(idc.getPartyCode());
                            return partyIdentifyingCode;
                          });
              return partyIdentifyingCodeRepository
                  .saveAll(
                      Flux.fromStream(
                          partyIdentifyingCodeStream)) // save identifying codes related to party
                  // obj
                  .map(
                      savedIdcs ->
                          PartyTO.IdentifyingCode.builder()
                              .partyCode(savedIdcs.getPartyCode())
                              .codeListName(savedIdcs.getCodeListName())
                              .dcsaResponsibleAgencyCode(savedIdcs.getDcsaResponsibleAgencyCode())
                              .build())
                  .collectList()
                  .flatMap(
                      identifyingCodes -> {
                        PartyTO pTO = t.getT2();
                        pTO.setIdentifyingCodes(identifyingCodes);
                        return Mono.just(Tuples.of(t.getT1(), pTO));
                      });
            });
  }

  private DocumentParty getDocumentPartyByRefColumn(
      Object id,
      ColumnReferenceType columnReferenceType,
      String partyID,
      DocumentPartyTO documentPartyTO) {
    DocumentParty documentParty = new DocumentParty();
    documentParty.setPartyID(partyID);
    documentParty.setPartyFunction(documentPartyTO.getPartyFunction());
    documentParty.setIsToBeNotified(documentPartyTO.getIsToBeNotified());

    switch (columnReferenceType) {
      case BOOKING:
        documentParty.setBookingID((UUID) id);
        break;
      case SHIPPING_INSTRUCTION:
        documentParty.setShippingInstructionID((UUID) id);
        break;
      case SHIPMENT:
        documentParty.setShipmentID((UUID) id);
        break;
      default:
    }

    return documentParty;
  }
}
