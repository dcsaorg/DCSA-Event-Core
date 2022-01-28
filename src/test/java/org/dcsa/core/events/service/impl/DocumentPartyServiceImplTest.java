package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.DCSAResponsibleAgencyCode;
import org.dcsa.core.events.model.enums.PartyFunction;
import org.dcsa.core.events.model.transferobjects.DocumentPartyTO;
import org.dcsa.core.events.model.transferobjects.PartyContactDetailsTO;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.AddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
public class DocumentPartyServiceImplTest {

  @Mock DocumentPartyRepository documentPartyRepository;
  @Mock DisplayedAddressRepository displayedAddressRepository;
  @Mock PartyRepository partyRepository;
  @Mock PartyIdentifyingCodeRepository partyIdentifyingCodeRepository;
  @Mock PartyContactDetailsRepository partyContactDetailsRepository;

  @Mock AddressService addressService;

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

}
