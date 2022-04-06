package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.config.MessageServiceConfig;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.SignatureMethod;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.DeleteException;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.exception.UpdateException;
import org.dcsa.core.service.impl.QueryServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventSubscriptionServiceImpl
    extends QueryServiceImpl<EventSubscriptionRepository, EventSubscription, UUID>
    implements EventSubscriptionService {
  // This is not guaranteed to be correct, but it will work "most of the time".
  private static final List<String> EMPTY_SQL_LIST = List.of("");

  private final EventSubscriptionRepository eventSubscriptionRepository;
  private final MessageServiceConfig messageServiceConfig;
  private final TransportCallRepository transportCallRepository;
  private final VoyageRepository voyageRepository;
  private final ServiceRepository serviceRepository;
  private final TransportDocumentRepository transportDocumentRepository;
  private final TransportDocumentTypeRepository transportDocumentTypeRepository;
  private final UtilizedTransportEquipmentRepository utilizedTransportEquipmentRepository;
  private final TransportRepository transportRepository;
  private final BookingRepository bookingRepository;

  @Override
  public EventSubscriptionRepository getRepository() {
    return eventSubscriptionRepository;
  }

  @Override
  public Mono<EventSubscription> findById(UUID id) {
    return eventSubscriptionRepository
        .findById(id)
        .switchIfEmpty(Mono.error(new GetException("No EventSubscription with ID: " + id)));
  }

  @Override
  public Mono<Void> deleteById(UUID subscriptionID) {
    return eventSubscriptionRepository
        .deleteById(subscriptionID)
        .switchIfEmpty(
            Mono.error(new DeleteException("No EventSubscription with ID: " + subscriptionID)));
  }

  @Override
  public Mono<EventSubscription> create(EventSubscription eventSubscription) {
    byte[] secret = eventSubscription.getSecret();
    SignatureMethod signatureMethod;
    if (eventSubscription.getSignatureMethod() == null) {
      signatureMethod = messageServiceConfig.getDefaultSignatureMethod();
      eventSubscription.setSignatureMethod(signatureMethod);
    } else {
      signatureMethod = eventSubscription.getSignatureMethod();
    }
    if (secret == null || secret.length < 1) {
      return Mono.error(new CreateException("Please provide a non-empty \"secret\" attribute"));
    }
    if (signatureMethod.getMinKeyLength() == signatureMethod.getMaxKeyLength()) {
      if (secret.length != signatureMethod.getMinKeyLength()) {
        if (secret.length > 1
            && secret.length - 1 == signatureMethod.getMinKeyLength()
            && Character.isSpaceChar(((int) secret[secret.length - 1]) & 0xff)) {
          return Mono.error(
              new CreateException(
                  "The secret provided in the \"secret\" attribute must be exactly "
                      + signatureMethod.getMinKeyLength()
                      + " bytes long (when deserialized).  Did you"
                      + " accidentally include a trailing newline/space in the secret?"));
        }
        return Mono.error(
            new CreateException(
                "The secret provided in the \"secret\" attribute must be exactly "
                    + signatureMethod.getMinKeyLength()
                    + " bytes long (when deserialized)"));
      }
    } else {
      if (secret.length < signatureMethod.getMinKeyLength()) {
        return Mono.error(
            new CreateException(
                "The secret provided in the \"secret\" attribute must be at least "
                    + signatureMethod.getMinKeyLength()
                    + " bytes long (when deserialized)"));
      }
      if (secret.length > signatureMethod.getMaxKeyLength()) {
        return Mono.error(
            new CreateException(
                "The secret provided in the \"secret\" attribute must be at most "
                    + signatureMethod.getMaxKeyLength()
                    + " bytes long (when deserialized)"));
      }
    }
    return checkEventSubscription(eventSubscription);
  }

  @Override
  public Mono<EventSubscription> update(EventSubscription update) {
    return checkEventSubscription(update).flatMap(eventSubscriptionRepository::save);
  }

  protected Mono<EventSubscription> checkEventSubscription(EventSubscription eventSubscription) {
    // Ensure that the callback url at least looks valid.
    try {
      new URI(eventSubscription.getCallbackUrl());
    } catch (URISyntaxException e) {
      return Mono.error(new UpdateException("callbackUrl is invalid: " + e.getLocalizedMessage()));
    }
    return Mono.just(eventSubscription);
  }

  @Override
  public Flux<EventSubscription> findSubscriptionsFor(Event event) {
    switch (event.getEventType()) {
      case EQUIPMENT:
        return findSubscriptionsFor((EquipmentEvent) event);
      case SHIPMENT:
        return findSubscriptionsFor((ShipmentEvent) event);
      case TRANSPORT:
        return findSubscriptionsFor((TransportEvent) event);
      case OPERATIONS:
        return findSubscriptionsFor((OperationsEvent) event);
      default:
        throw new IllegalArgumentException("Unsupported event type.");
    }
  }

  private Flux<EventSubscription> findSubscriptionsFor(EquipmentEvent equipmentEvent) {

    Mono<List<String>> carrierVoyageNumbers =
        transportCallRepository
            .findCarrierVoyageNumbersByTransportCallID(equipmentEvent.getTransportCallID())
            .collectList();

    Mono<List<String>> carrierServiceCodes =
        transportCallRepository
            .findCarrierServiceCodesByTransportCallID(equipmentEvent.getTransportCallID())
            .collectList();

    String vesselIMONumber = equipmentEvent.getTransportCall().getVessel().getVesselIMONumber();

    List<DocumentReferenceTO> documentReferences = equipmentEvent.getDocumentReferences();

    List<String> carrierBookingReferences =
        documentReferences.stream()
            .filter(
                documentReferenceTO ->
                    documentReferenceTO.getDocumentReferenceType() == DocumentReferenceType.BKG)
            .map(DocumentReferenceTO::getDocumentReferenceValue)
            .collect(Collectors.toList());

    List<String> transportDocumentReferences =
        documentReferences.stream()
            .filter(
                documentReferenceTO ->
                    documentReferenceTO.getDocumentReferenceType() == DocumentReferenceType.TRD)
            .map(DocumentReferenceTO::getDocumentReferenceValue)
            .collect(Collectors.toList());

    Mono<List<String>> transportDocumentTypeCodes =
        Flux.fromIterable(transportDocumentReferences)
            .flatMap(
                transportCallRepository::findTransportDocumentTypeCodeByTransportDocumentReference)
            .collectList();

    return Mono.zip(carrierVoyageNumbers, carrierServiceCodes, transportDocumentTypeCodes)
        .flatMapMany(
            vnScTc -> {
              List<String> voyageNumbers = vnScTc.getT1();
              List<String> serviceCodes = vnScTc.getT2();
              List<String> documentTypeCodes = vnScTc.getT3();
              List<String> carrierBookingRefs = carrierBookingReferences;
              List<String> transportDocumentRefs = transportDocumentReferences;
              // Force these to be null when empty to avoid invalid queries
              if (voyageNumbers.isEmpty()) {
                voyageNumbers = emptySQLList();
              }
              if (serviceCodes.isEmpty()) {
                serviceCodes = emptySQLList();
              }
              if (documentTypeCodes.isEmpty()) {
                documentTypeCodes = emptySQLList();
              }
              if (carrierBookingRefs.isEmpty()) {
                carrierBookingRefs = emptySQLList();
              }
              if (transportDocumentRefs.isEmpty()) {
                transportDocumentRefs = emptySQLList();
              }

              return eventSubscriptionRepository.findByEquipmentEventFields(
                  equipmentEvent.getEquipmentEventTypeCode(),
                  equipmentEvent.getEquipmentReference(),
                  carrierBookingRefs,
                  transportDocumentRefs,
                  documentTypeCodes,
                  equipmentEvent.getTransportCallID(),
                  vesselIMONumber,
                  voyageNumbers,
                  serviceCodes);
            });
  }

  private Flux<EventSubscription> findSubscriptionsFor(ShipmentEvent shipmentEvent) {

    Mono<List<String>> carrierBookingReferences;
    Mono<List<String>> carrierVoyageNumbers;
    Mono<List<String>> carrierServiceCodes;
    Mono<List<String>> transportDocumentReferences;
    Mono<List<String>> transportDocumentTypeCodes;
    Mono<List<String>> transportCallIDs;
    Mono<List<String>> equipmentReferences;
    Mono<List<String>> vesselIMONumbers;

    switch (shipmentEvent.getDocumentTypeCode()) {
      case BKG:
        carrierBookingReferences =
            Mono.just(Collections.singletonList(shipmentEvent.getDocumentReference()));
        carrierVoyageNumbers =
            voyageRepository
                .findCarrierVoyageNumbersByCarrierBookingRef(shipmentEvent.getDocumentReference())
                .collectList();
        carrierServiceCodes =
            serviceRepository
                .findCarrierServiceCodesByCarrierBookingRef(shipmentEvent.getDocumentReference())
                .collectList();
        transportDocumentReferences =
            transportDocumentRepository
                .findTransportDocumentReferencesByCarrierBookingRef(
                    shipmentEvent.getDocumentReference())
                .collectList();
        transportDocumentTypeCodes =
            transportDocumentTypeRepository
                .findCodesByCarrierBookingRef(shipmentEvent.getDocumentReference())
                .collectList();
        transportCallIDs =
            transportCallRepository
                .findTransportCallIDByCarrierBookingRef(shipmentEvent.getDocumentReference())
                .collectList();
        equipmentReferences =
            utilizedTransportEquipmentRepository
                .findEquipmentReferenceByCarrierBookRef(shipmentEvent.getDocumentReference())
                .collectList();
        vesselIMONumbers =
            transportRepository
                .findVesselIMONumbersByCarrierBookingRef(shipmentEvent.getDocumentReference())
                .collectList();
        break;
      case SHI:
        carrierBookingReferences =
            bookingRepository
                .findCarrierBookingRefsByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();
        carrierVoyageNumbers =
            voyageRepository
                .findCarrierVoyageNumbersByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();
        carrierServiceCodes =
            serviceRepository
                .findCarrierServiceCodesByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();

        transportDocumentReferences =
            transportDocumentRepository
                .findDistinctTransportDocumentReferencesByShippingInstructionReference(
                    shipmentEvent.getDocumentReference())
                .map(TransportDocument::getTransportDocumentReference)
                .collectList();

        transportDocumentTypeCodes =
            transportDocumentTypeRepository
                .findCodesByShippingInstructionReference(shipmentEvent.getDocumentReference())
                .collectList();
        transportCallIDs =
            transportCallRepository
                .findTransportCallIDByShippingInstructionReference(
                    shipmentEvent.getDocumentReference())
                .collectList();
        equipmentReferences =
            utilizedTransportEquipmentRepository
                .findEquipmentReferenceByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();
        vesselIMONumbers =
            transportRepository
                .findVesselIMONumbersByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();

        break;
      case TRD:
        carrierBookingReferences =
            bookingRepository
                .findCarrierBookingRefsByTransportDocumentRef(shipmentEvent.getDocumentReference())
                .collectList();
        carrierVoyageNumbers =
            voyageRepository
                .findCarrierVoyageNumbersByTransportDocumentRef(
                    shipmentEvent.getDocumentReference())
                .collectList();

        carrierServiceCodes =
            serviceRepository
                .findCarrierServiceCodesByTransportDocumentRef(shipmentEvent.getDocumentReference())
                .collectList();

        transportDocumentReferences =
            transportDocumentRepository
                .findDistinctTransportDocumentReferencesByTransportDocumentReference(
                    shipmentEvent.getDocumentReference())
                .map(TransportDocument::getTransportDocumentReference)
                .collectList();

        transportDocumentTypeCodes =
            transportDocumentTypeRepository
                .findCodesByTransportDocumentReference(shipmentEvent.getDocumentReference())
                .collectList();

        transportCallIDs =
            transportCallRepository
                .findTransportCallIDByTransportDocumentRef(shipmentEvent.getDocumentReference())
                .collectList();
        equipmentReferences =
            utilizedTransportEquipmentRepository
                .findEquipmentReferenceByTransportDocumentRef(shipmentEvent.getDocumentReference())
                .collectList();
        vesselIMONumbers =
            transportRepository
                .findVesselIMONumbersByTransportDocumentRef(shipmentEvent.getDocumentReference())
                .collectList();
        break;
      default:
        // do nothing
        return Flux.error(
            new UnsupportedOperationException(
                "Document type code can only be one of BKG, SHI or TRD for shipment event."));
    }

    return Mono.zip(
            carrierBookingReferences,
            transportDocumentReferences,
            transportDocumentTypeCodes,
            transportCallIDs,
            equipmentReferences,
            carrierServiceCodes,
            carrierVoyageNumbers,
            vesselIMONumbers)
        .flatMapMany(
            params ->
                eventSubscriptionRepository.findByShipmentEventFields(
                    shipmentEvent.getShipmentEventTypeCode(),
                    params.getT1(),
                    params.getT2(),
                    params.getT3(),
                    params.getT4(),
                    params.getT5(),
                    params.getT6(),
                    params.getT7(),
                    params.getT8()));
  }

  private Flux<EventSubscription> findSubscriptionsFor(TransportEvent transportEvent) {
    Mono<List<String>> carrierVoyageNumbers =
        transportCallRepository
            .findCarrierVoyageNumbersByTransportCallID(transportEvent.getTransportCallID())
            .collectList();
    Mono<List<String>> carrierServiceCodes =
        transportCallRepository
            .findCarrierServiceCodesByTransportCallID(transportEvent.getTransportCallID())
            .collectList();

    TransportEventTypeCode transportEventTypeCode = transportEvent.getTransportEventTypeCode();
    String vesselIMONumber = transportEvent.getTransportCall().getVessel().getVesselIMONumber();
    String transportCallID = transportEvent.getTransportCallID();

    List<DocumentReferenceTO> documentReferences = transportEvent.getDocumentReferences();
    List<String> carrierBookingReferences =
        documentReferences.stream()
            .filter(
                documentReferenceTO ->
                    documentReferenceTO.getDocumentReferenceType() == DocumentReferenceType.BKG)
            .map(DocumentReferenceTO::getDocumentReferenceValue)
            .collect(Collectors.toList());
    List<String> transportDocumentReferences =
        documentReferences.stream()
            .filter(
                documentReferenceTO ->
                    documentReferenceTO.getDocumentReferenceType() == DocumentReferenceType.TRD)
            .map(DocumentReferenceTO::getDocumentReferenceValue)
            .collect(Collectors.toList());

    Mono<List<String>> transportDocumentTypeCodes =
        Flux.fromIterable(transportDocumentReferences)
            .flatMap(
                transportCallRepository::findTransportDocumentTypeCodeByTransportDocumentReference)
            .collectList();

    return Mono.zip(carrierVoyageNumbers, carrierServiceCodes, transportDocumentTypeCodes)
        .flatMapMany(
            vnScTc -> {
              List<String> voyageNumbers = vnScTc.getT1();
              List<String> serviceCodes = vnScTc.getT2();
              List<String> documentTypeCodes = vnScTc.getT3();
              List<String> carrierBookingRefs = carrierBookingReferences;
              List<String> transportDocumentRefs = transportDocumentReferences;
              // Force these to be null when empty to avoid invalid queries
              if (voyageNumbers.isEmpty()) {
                voyageNumbers = emptySQLList();
              }
              if (serviceCodes.isEmpty()) {
                serviceCodes = emptySQLList();
              }
              if (documentTypeCodes.isEmpty()) {
                documentTypeCodes = emptySQLList();
              }
              if (carrierBookingRefs.isEmpty()) {
                carrierBookingRefs = emptySQLList();
              }
              if (transportDocumentRefs.isEmpty()) {
                transportDocumentRefs = emptySQLList();
              }

              return eventSubscriptionRepository.findByTransportEventFields(
                  voyageNumbers,
                  serviceCodes,
                  transportEventTypeCode,
                  vesselIMONumber,
                  transportCallID,
                  carrierBookingRefs,
                  transportDocumentRefs,
                  documentTypeCodes);
            });
  }

  private Flux<EventSubscription> findSubscriptionsFor(OperationsEvent operationsEvent) {
    Mono<List<String>> carrierVoyageNumbers =
        transportCallRepository
            .findCarrierVoyageNumbersByTransportCallID(operationsEvent.getTransportCallID())
            .collectList();

    Mono<List<String>> carrierServiceCodes =
        transportCallRepository
            .findCarrierServiceCodesByTransportCallID(operationsEvent.getTransportCallID())
            .collectList();
    OperationsEventTypeCode operationsEventTypeCode = operationsEvent.getOperationsEventTypeCode();

    String vesselIMONumber = operationsEvent.getTransportCall().getVessel().getVesselIMONumber();

    String transportCallID = operationsEvent.getTransportCallID();

    Mono<List<String>> carrierBookingReferences =
        bookingRepository
            .findCarrierBookingRefsByTransportCallID(operationsEvent.getTransportCallID())
            .collectList();
    Mono<List<String>> transportDocumentReferences =
        transportDocumentRepository
            .findTransportDocumentReferencesByTransportCallID(operationsEvent.getTransportCallID())
            .collectList();
    Mono<List<String>> transportDocumentTypeCodes =
        transportDocumentTypeRepository
            .findCodesByTransportCallID(operationsEvent.getTransportCallID())
            .collectList();

    return Mono.zip(
            carrierVoyageNumbers,
            carrierServiceCodes,
            carrierBookingReferences,
            transportDocumentReferences,
            transportDocumentTypeCodes)
        .flatMapMany(
            params -> {
              List<String> cvns = params.getT1();
              List<String> cscs = params.getT2();
              List<String> cbrs = params.getT3();
              List<String> tdrs = params.getT4();
              List<String> tdtcs = params.getT5();

              // Force these to be null when empty to avoid invalid queries
              if (cvns.isEmpty()) {
                cvns = emptySQLList();
              }
              if (cscs.isEmpty()) {
                cscs = emptySQLList();
              }
              if (cbrs.isEmpty()) {
                cbrs = emptySQLList();
              }
              if (tdrs.isEmpty()) {
                tdrs = emptySQLList();
              }
              if (tdtcs.isEmpty()) {
                tdtcs = emptySQLList();
              }

              return eventSubscriptionRepository.findByOperationEventFields(
                  operationsEventTypeCode,
                  cbrs,
                  tdrs,
                  tdtcs,
                  transportCallID,
                  vesselIMONumber,
                  cvns,
                  cscs);
            });
  }

  @SuppressWarnings({"unchecked"})
  private static <E> List<E> emptySQLList() {
    return (List<E>) EMPTY_SQL_LIST;
  }
}
