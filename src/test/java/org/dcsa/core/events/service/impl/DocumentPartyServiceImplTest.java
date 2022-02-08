package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.ColumnReferenceType;
import org.dcsa.core.events.model.enums.DCSAResponsibleAgencyCode;
import org.dcsa.core.events.model.enums.PartyFunction;
import org.dcsa.core.events.model.mapper.PartyMapper;
import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import org.dcsa.core.events.model.transferobjects.PartyContactDetailsTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test for DocumentPartyService implementation")
class DocumentPartyServiceImplTest {

  @Mock DocumentPartyRepository documentPartyRepository;
  @Mock DisplayedAddressRepository displayedAddressRepository;
  @Mock PartyRepository partyRepository;
  @Mock PartyIdentifyingCodeRepository partyIdentifyingCodeRepository;
  @Mock PartyContactDetailsRepository partyContactDetailsRepository;

  @Mock AddressService addressService;

  @Spy PartyMapper partyMapper = Mappers.getMapper(PartyMapper.class);

  @InjectMocks DocumentPartyServiceImpl documentPartyService;

  List<DocumentPartyTO> documentParties;
  PartyContactDetailsTO partyContactDetailsTO;
  PartyTO partyTO;
  PartyTO.IdentifyingCode partyIdentifyingCodeTO;
  DocumentPartyTO documentPartyTO;

  DisplayedAddress displayedAddress;
  Party party;
  PartyContactDetails partyContactDetails;
  DocumentParty documentParty;
  PartyIdentifyingCode partyIdentifyingCode;
  Address address;

  @BeforeEach
  void init() {

    initEntities();

    initTO();
  }

  private void initTO() {
    Address address = new Address();
    address.setId(UUID.fromString("8fecc6d0-2a78-401d-948a-b9753f6b53d5"));
    address.setName("Fraz");
    address.setStreet("Kronprincessegade");
    address.setPostalCode("1306");
    address.setCity("København");
    address.setCountry("Denmark");

    partyContactDetailsTO = new PartyContactDetailsTO();
    partyContactDetailsTO.setName("Bit");
    partyContactDetailsTO.setEmail("coin@gmail.com");

    partyIdentifyingCodeTO =
        PartyTO.IdentifyingCode.builder()
            .dcsaResponsibleAgencyCode(DCSAResponsibleAgencyCode.ISO)
            .codeListName("LCL")
            .partyCode("MSK")
            .build();

    partyTO = new PartyTO();
    partyTO.setPartyName("DCSA");
    partyTO.setAddress(address);
    partyTO.setPartyContactDetails(Collections.singletonList(partyContactDetailsTO));
    partyTO.setIdentifyingCodes(Collections.singletonList(partyIdentifyingCodeTO));

    documentPartyTO = new DocumentPartyTO();
    documentPartyTO.setParty(partyTO);
    documentPartyTO.setPartyFunction(PartyFunction.DDS);
    documentPartyTO.setDisplayedAddress(Stream.of("test 1", "test 2").collect(Collectors.toList()));
    documentPartyTO.setIsToBeNotified(true);

    documentParties = Collections.singletonList(documentPartyTO);
  }

  private void initEntities() {

    address = new Address();
    address.setId(UUID.fromString("8fecc6d0-2a78-401d-948a-b9753f6b53d5"));
    address.setName("Fraz");
    address.setStreet("Kronprincessegade");
    address.setPostalCode("1306");
    address.setCity("København");
    address.setCountry("Denmark");

    party = new Party();
    party.setId("a680fe72-503e-40b3-9cfc-dcadafdecf15");
    party.setPartyName("DCSA");
    party.setAddressID(address.getId());

    documentParty = new DocumentParty();
    documentParty.setId(UUID.fromString("3d9542f8-c362-4fa5-8902-90e30d87f1d4"));
    documentParty.setPartyID("d04fb8c6-eb9c-474d-9cf7-86aa6bfcc2a2");
    documentParty.setPartyFunction(PartyFunction.DDS);

    partyIdentifyingCode = new PartyIdentifyingCode();
    partyIdentifyingCode.setPartyID(party.getId());
    partyIdentifyingCode.setCodeListName("LCL");
    partyIdentifyingCode.setDcsaResponsibleAgencyCode(DCSAResponsibleAgencyCode.ISO);
    partyIdentifyingCode.setPartyCode("MSK");

    displayedAddress = new DisplayedAddress();
    displayedAddress.setDocumentPartyID(documentParty.getId());
    displayedAddress.setAddressLine("Javastraat");
    displayedAddress.setAddressLineNumber(1);

    partyContactDetails = new PartyContactDetails();
    partyContactDetails.setName("Peanut");
    partyContactDetails.setEmail("peanut@jeff-fa-fa.com");
  }

  @Test
  @DisplayName("Test create DocumentParty with bookingID")
  void testWithBookingID() {
    UUID bookingID = UUID.randomUUID();
    documentParty.setBookingID(bookingID);

    when(partyRepository.save(any())).thenReturn(Mono.just(party));
    when(partyContactDetailsRepository.saveAll(any(Flux.class))).thenReturn(Flux.just(partyContactDetails));
    when(partyIdentifyingCodeRepository.saveAll(any(Flux.class))).thenReturn(Flux.just(partyIdentifyingCode));
    when(documentPartyRepository.save(any())).thenReturn(Mono.just(documentParty));
    when(displayedAddressRepository.saveAll(any(Flux.class))).thenReturn(Flux.just(displayedAddress));
    when(addressService.ensureResolvable(any())).thenReturn(Mono.just(address));

    ArgumentCaptor<DocumentParty> argumentCaptor = ArgumentCaptor.forClass(DocumentParty.class);

    StepVerifier.create(
            documentPartyService.createDocumentPartiesByBookingID(bookingID, documentParties))
        .assertNext(
            documentPartyTOS -> {
              verify(addressService).ensureResolvable(any());
              verify(partyRepository).save(any());
              verify(partyContactDetailsRepository).saveAll(any(Flux.class));
              verify(partyIdentifyingCodeRepository).saveAll(any(Flux.class));
              verify(documentPartyRepository).save(any());
              verify(displayedAddressRepository).saveAll(any(Flux.class));

              verify(documentPartyRepository).save(argumentCaptor.capture());
              assertEquals(bookingID, argumentCaptor.getValue().getBookingID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create DocumentParty with shippingInstructionID")
  void testWithShippingInstructionID() {
    String shippingInstructionId = UUID.randomUUID().toString();
    documentParty.setShippingInstructionID(shippingInstructionId);

    when(partyRepository.save(any())).thenReturn(Mono.just(party));
    when(partyContactDetailsRepository.saveAll(any(Flux.class)))
        .thenReturn(Flux.just(partyContactDetails));
    when(partyIdentifyingCodeRepository.saveAll(any(Flux.class)))
        .thenReturn(Flux.just(partyIdentifyingCode));
    when(documentPartyRepository.save(any())).thenReturn(Mono.just(documentParty));
    when(displayedAddressRepository.saveAll(any(Flux.class)))
        .thenReturn(Flux.just(displayedAddress));
    when(addressService.ensureResolvable(any())).thenReturn(Mono.just(address));

    ArgumentCaptor<DocumentParty> argumentCaptor = ArgumentCaptor.forClass(DocumentParty.class);

    StepVerifier.create(
            documentPartyService.createDocumentPartiesByShippingInstructionID(
                shippingInstructionId, documentParties))
        .assertNext(
            documentPartyTOS -> {
              verify(addressService).ensureResolvable(any());
              verify(partyRepository).save(any());
              verify(partyContactDetailsRepository).saveAll(any(Flux.class));
              verify(partyIdentifyingCodeRepository).saveAll(any(Flux.class));
              verify(documentPartyRepository).save(any());
              verify(displayedAddressRepository).saveAll(any(Flux.class));

              verify(documentPartyRepository).save(argumentCaptor.capture());
              assertEquals(
                  shippingInstructionId, argumentCaptor.getValue().getShippingInstructionID());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test create DocumentParty withoutDisplayedAddress")
  void testWithShippingInstructionIDWithoutDisplayedAddress() {
    String shippingInstructionId = UUID.randomUUID().toString();
    documentParty.setShippingInstructionID(shippingInstructionId);
    DocumentPartyTO documentPartyTOWithoutDisplayedAddress = new DocumentPartyTO();
    documentPartyTOWithoutDisplayedAddress.setParty(documentPartyTO.getParty());
    documentPartyTOWithoutDisplayedAddress.setPartyFunction(documentPartyTO.getPartyFunction());

    when(partyRepository.save(any())).thenReturn(Mono.just(party));
    when(partyContactDetailsRepository.saveAll(any(Flux.class)))
      .thenReturn(Flux.just(partyContactDetails));
    when(partyIdentifyingCodeRepository.saveAll(any(Flux.class)))
      .thenReturn(Flux.just(partyIdentifyingCode));
    when(documentPartyRepository.save(any())).thenReturn(Mono.just(documentParty));
    when(displayedAddressRepository.saveAll(any(Flux.class)))
      .thenReturn(Flux.empty());
    when(addressService.ensureResolvable(any())).thenReturn(Mono.just(address));

    ArgumentCaptor<DocumentParty> argumentCaptor = ArgumentCaptor.forClass(DocumentParty.class);

    StepVerifier.create(
        documentPartyService.createDocumentPartiesByShippingInstructionID(
          shippingInstructionId, Collections.singletonList(documentPartyTOWithoutDisplayedAddress)))
      .assertNext(
        documentPartyTOS -> {
          verify(addressService).ensureResolvable(any());
          verify(partyRepository).save(any());
          verify(partyContactDetailsRepository).saveAll(any(Flux.class));
          verify(partyIdentifyingCodeRepository).saveAll(any(Flux.class));
          verify(documentPartyRepository).save(any());

          verify(documentPartyRepository).save(argumentCaptor.capture());
          assertEquals(
            shippingInstructionId, argumentCaptor.getValue().getShippingInstructionID());
        })
      .verifyComplete();
  }

  @Test
  @DisplayName("Test create DocumentParty with minimal party.")
  void testWithMinimalPartyInDocumentParty() {
    String shippingInstructionId = UUID.randomUUID().toString();
    documentParty.setShippingInstructionID(shippingInstructionId);
    party.setAddressID(null);

    PartyContactDetailsTO minimalPartyContactDetails = new PartyContactDetailsTO();
    minimalPartyContactDetails.setName("dummyName");
    PartyTO minimalParty = new PartyTO();
    minimalParty.setPartyContactDetails(Collections.singletonList(minimalPartyContactDetails));
    minimalParty.setIdentifyingCodes(Collections.singletonList(partyIdentifyingCodeTO));

    DocumentPartyTO documentPartyTOWithMinParty = new DocumentPartyTO();
    documentPartyTOWithMinParty.setParty(minimalParty);

    when(partyRepository.save(any())).thenReturn(Mono.just(party));
    when(partyContactDetailsRepository.saveAll(any(Flux.class)))
      .thenReturn(Flux.just(partyContactDetails));
    when(partyIdentifyingCodeRepository.saveAll(any(Flux.class)))
      .thenReturn(Flux.just(partyIdentifyingCode));
    when(documentPartyRepository.save(any())).thenReturn(Mono.just(documentParty));
    when(displayedAddressRepository.saveAll(any(Flux.class)))
      .thenReturn(Flux.just(displayedAddress));

    ArgumentCaptor<DocumentParty> argumentCaptor = ArgumentCaptor.forClass(DocumentParty.class);

    StepVerifier.create(
        documentPartyService.createDocumentPartiesByShippingInstructionID(
          shippingInstructionId, Collections.singletonList(documentPartyTOWithMinParty)))
      .assertNext(
        documentPartyTOS -> {
          verify(partyRepository).save(any());
          verify(partyContactDetailsRepository).saveAll(any(Flux.class));
          verify(partyIdentifyingCodeRepository).saveAll(any(Flux.class));
          verify(documentPartyRepository).save(any());
          verify(displayedAddressRepository).saveAll(any(Flux.class));

          verify(documentPartyRepository).save(argumentCaptor.capture());
          assertEquals(
            shippingInstructionId, argumentCaptor.getValue().getShippingInstructionID());
        })
      .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch party contact details by party ID.")
  void testFetchPartyContactDetailsByPartyID() {

    when(partyContactDetailsRepository.findByPartyID(any()))
        .thenReturn(Flux.just(partyContactDetails));

    StepVerifier.create(documentPartyService.fetchPartyContactDetailsByPartyID(party.getId()))
        .assertNext(
            dpTOs -> {
              verify(partyContactDetailsRepository).findByPartyID(any());
              assertEquals("Peanut", dpTOs.get(0).getName());
              assertEquals("peanut@jeff-fa-fa.com", dpTOs.get(0).getEmail());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch party contact details by party ID returns empty list.")
  void testFetchPartyContactDetailsByPartyIDEmptyList() {

    when(partyContactDetailsRepository.findByPartyID(any())).thenReturn(Flux.empty());

    StepVerifier.create(documentPartyService.fetchPartyContactDetailsByPartyID(party.getId()))
        .assertNext(
            dpTOs -> {
              verify(partyContactDetailsRepository).findByPartyID(any());
              assertEquals(0, dpTOs.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch identifying codes by party ID.")
  void testFetchIdentifyingCodesByPartyId() {

    when(partyIdentifyingCodeRepository.findAllByPartyID(any()))
        .thenReturn(Flux.just(partyIdentifyingCode));

    StepVerifier.create(documentPartyService.fetchIdentifyingCodesByPartyID(party.getId()))
        .assertNext(
            idcTOs -> {
              verify(partyIdentifyingCodeRepository).findAllByPartyID(any());
              assertEquals("MSK", idcTOs.get(0).getPartyCode());
              assertEquals("LCL", idcTOs.get(0).getCodeListName());
              assertEquals(
                  DCSAResponsibleAgencyCode.ISO, idcTOs.get(0).getDcsaResponsibleAgencyCode());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch identifying codes by party ID returns empty list.")
  void testFetchIdentifyingCodesByPartyIdEmptyList() {

    when(partyIdentifyingCodeRepository.findAllByPartyID(any())).thenReturn(Flux.empty());

    StepVerifier.create(documentPartyService.fetchIdentifyingCodesByPartyID(party.getId()))
        .assertNext(
            idcTOs -> {
              verify(partyIdentifyingCodeRepository).findAllByPartyID(any());
              assertEquals(0, idcTOs.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch Display Address by Document ID.")
  void testFetchDisplayAddressByDocumentID() {
    when(displayedAddressRepository.findByDocumentPartyIDOrderByAddressLineNumber(any()))
        .thenReturn(Flux.just(displayedAddress));

    StepVerifier.create(documentPartyService.fetchDisplayAddressByDocumentID(documentParty.getId()))
        .assertNext(
            da -> {
              verify(displayedAddressRepository)
                  .findByDocumentPartyIDOrderByAddressLineNumber(any());
              assertEquals("Javastraat", da.get(0));
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch Display Address by Document ID returns empty list.")
  void testFetchDisplayAddressByDocumentIDEmptyList() {
    when(displayedAddressRepository.findByDocumentPartyIDOrderByAddressLineNumber(any()))
        .thenReturn(Flux.empty());

    StepVerifier.create(documentPartyService.fetchDisplayAddressByDocumentID(documentParty.getId()))
        .assertNext(
            da -> {
              verify(displayedAddressRepository)
                  .findByDocumentPartyIDOrderByAddressLineNumber(any());
              assertEquals(0, da.size());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch party by ID.")
  void testFetchPartyByID() {

    when(partyRepository.findByIdOrEmpty(any())).thenReturn(Mono.just(party));
    when(addressService.findByIdOrEmpty(any())).thenReturn(Mono.just(address));
    when(partyIdentifyingCodeRepository.findAllByPartyID(any()))
        .thenReturn(Flux.just(partyIdentifyingCode));
    when(partyContactDetailsRepository.findByPartyID(any()))
        .thenReturn(Flux.just(partyContactDetails));

    StepVerifier.create(documentPartyService.fetchPartyByID(party.getId()))
        .assertNext(
            pTO -> {
              verify(partyRepository).findByIdOrEmpty(any());
              verify(addressService).findByIdOrEmpty(any());
              verify(partyIdentifyingCodeRepository).findAllByPartyID(any());
              verify(partyContactDetailsRepository).findByPartyID(any());
              assertEquals("DCSA", pTO.getPartyName());
              assertEquals(1, pTO.getPartyContactDetails().size());
              assertEquals(1, pTO.getIdentifyingCodes().size());
              assertEquals("Denmark", pTO.getAddress().getCountry());
            })
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch party by null ID should return empty mono.")
  void testFetchPartyByIDNull() {
    StepVerifier.create(documentPartyService.fetchPartyByID(null))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch party by ID should return empty mono if no parties linked to ID.")
  void testFetchPartyByIDNoLinkedParties() {

    when(partyRepository.findByIdOrEmpty(any())).thenReturn(Mono.empty());

    StepVerifier.create(documentPartyService.fetchPartyByID(party.getId()))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch document parties by ID should return empty mono for null ID.")
  void testFetchDocumentPartiesByIDNull() {
    StepVerifier.create(
            documentPartyService.fetchDocumentPartiesByID(
                null, ColumnReferenceType.SHIPPING_INSTRUCTION))
        .expectNextCount(0)
        .verifyComplete();
  }

  @Test
  @DisplayName("Test fetch document parties by ID.")
  void testFetchDocumentPartiesByID() {

    when(documentPartyRepository.findByShippingInstructionID(any()))
        .thenReturn(Flux.just(documentParty));

    when(partyRepository.findByIdOrEmpty(any())).thenReturn(Mono.just(party));
    when(addressService.findByIdOrEmpty(any())).thenReturn(Mono.just(address));
    when(partyIdentifyingCodeRepository.findAllByPartyID(any()))
        .thenReturn(Flux.just(partyIdentifyingCode));
    when(partyContactDetailsRepository.findByPartyID(any()))
        .thenReturn(Flux.just(partyContactDetails));

    when(displayedAddressRepository.findByDocumentPartyIDOrderByAddressLineNumber(any()))
        .thenReturn(Flux.just(displayedAddress));

    StepVerifier.create(
            documentPartyService.fetchDocumentPartiesByID(
                UUID.randomUUID().toString(), ColumnReferenceType.SHIPPING_INSTRUCTION))
        .assertNext(dpTOs -> {
            verify(documentPartyRepository).findByShippingInstructionID(any());
            assertEquals(PartyFunction.DDS, dpTOs.get(0).getPartyFunction());
            assertEquals(List.of("Javastraat"), dpTOs.get(0).getDisplayedAddress());
            assertEquals("DCSA", dpTOs.get(0).getParty().getPartyName());
        })
        .verifyComplete();
  }

}
