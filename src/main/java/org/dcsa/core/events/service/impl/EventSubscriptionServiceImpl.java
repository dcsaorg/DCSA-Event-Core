package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.config.MessageServiceConfig;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.DocumentReferenceType;
import org.dcsa.core.events.model.enums.SignatureMethod;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.model.transferobjects.DocumentReferenceTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.UpdateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.util.ValidationUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventSubscriptionServiceImpl extends ExtendedBaseServiceImpl<EventSubscriptionRepository, EventSubscription, UUID> implements EventSubscriptionService {
    private final EventSubscriptionRepository eventSubscriptionRepository;
    private final MessageServiceConfig messageServiceConfig;
    private final TransportCallRepository transportCallRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final VoyageRepository voyageRepository;
    private final ServiceRepository serviceRepository;
    private final TransportDocumentRepository transportDocumentRepository;
    private final TransportDocumentTypeRepository transportDocumentTypeRepository;
    private final ShipmentEquipmentRepository shipmentEquipmentRepository;
    private final TransportRepository transportRepository;

    @Override
    public EventSubscriptionRepository getRepository() {
        return eventSubscriptionRepository;
    }

    @Override
    protected Mono<EventSubscription> preCreateHook(EventSubscription eventSubscription) {
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
                if (secret.length  > 1 && secret.length - 1 == signatureMethod.getMinKeyLength()
                        && Character.isSpaceChar(((int)secret[secret.length - 1]) & 0xff)) {
                    return Mono.error(new CreateException("The secret provided in the \"secret\" attribute must be exactly "
                            + signatureMethod.getMinKeyLength() + " bytes long (when deserialized).  Did you"
                            + " accidentally include a trailing newline/space in the secret?"));
                }
                return Mono.error(new CreateException("The secret provided in the \"secret\" attribute must be exactly "
                        + signatureMethod.getMinKeyLength() + " bytes long (when deserialized)"));
            }
        } else {
            if (secret.length < signatureMethod.getMinKeyLength()) {
                return Mono.error(new CreateException("The secret provided in the \"secret\" attribute must be at least "
                        + signatureMethod.getMinKeyLength() + " bytes long (when deserialized)"));
            }
            if (secret.length > signatureMethod.getMaxKeyLength()) {
                return Mono.error(new CreateException("The secret provided in the \"secret\" attribute must be at most "
                        + signatureMethod.getMaxKeyLength() + " bytes long (when deserialized)"));
            }
        }
        return checkEventSubscription(eventSubscription);
    }

    @Override
    protected Mono<EventSubscription> preUpdateHook(EventSubscription original, EventSubscription update) {
        return checkEventSubscription(update);
    }

    protected Mono<EventSubscription> checkEventSubscription(EventSubscription eventSubscription) {
        String vessel = eventSubscription.getVesselIMONumber();
        if (vessel != null){
            try{
                ValidationUtils.validateVesselIMONumber(vessel);
            } catch (Exception e){
                return Mono.error(new UpdateException(e.getLocalizedMessage()));
            }
        }
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
        switch (event.getEventType()){
            case EQUIPMENT:
                return findSubscriptionsFor((EquipmentEvent) event);
            case SHIPMENT:
                return findSubscriptionsFor((ShipmentEvent) event);
            case TRANSPORT:
                return findSubscriptionsFor((TransportEvent) event);
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

              return eventSubscriptionRepository.findByEquipmentEventFields(
                  equipmentEvent.getEventType(),
                  equipmentEvent.getEquipmentEventTypeCode(),
                  equipmentEvent.getEquipmentReference(),
                  carrierBookingReferences,
                  transportDocumentReferences,
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
            Mono.just(Collections.singletonList(shipmentEvent.getDocumentID()));
        carrierVoyageNumbers =
            voyageRepository
                .findCarrierVoyageNumbersByCarrierBookingRef(shipmentEvent.getDocumentID())
                .collectList();
        carrierServiceCodes =
            serviceRepository
                .findCarrierServiceCodesByCarrierBookingRef(shipmentEvent.getDocumentID())
                .collectList();
        transportDocumentReferences =
            transportDocumentRepository
                .findTransportDocumentReferencesByCarrierBookingRef(shipmentEvent.getDocumentID())
                .collectList();
        transportDocumentTypeCodes =
            transportDocumentTypeRepository
                .findCodesByCarrierBookingRef(shipmentEvent.getDocumentID())
                .collectList();
        transportCallIDs =
            transportCallRepository
                .findTransportCallIDByCarrierBookingRef(shipmentEvent.getDocumentID())
                .collectList();
        equipmentReferences =
            shipmentEquipmentRepository
                .findEquipmentReferenceByCarrierBookRef(shipmentEvent.getDocumentID())
                .collectList();
        vesselIMONumbers =
            transportRepository
                .findVesselIMONumbersByCarrierBookingRef(shipmentEvent.getDocumentID())
                .collectList();
        break;
      case SHI:
        carrierBookingReferences =
            shipmentEventRepository
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
                .findDistinctTransportDocumentReferencesByShippingInstructionID(
                    shipmentEvent.getDocumentID())
                .map(TransportDocument::getTransportDocumentReference)
                .collectList();

        transportDocumentTypeCodes =
            transportDocumentTypeRepository
                .findCodesByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();
        transportCallIDs =
            transportCallRepository
                .findTransportCallIDByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();
        equipmentReferences =
            shipmentEquipmentRepository
                .findEquipmentReferenceByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();
        vesselIMONumbers =
            transportRepository
                .findVesselIMONumbersByShippingInstructionID(shipmentEvent.getDocumentID())
                .collectList();

        break;
      case TRD:
        carrierBookingReferences =
            shipmentEventRepository
                .findCarrierBookingRefsByTransportDocumentRef(shipmentEvent.getDocumentID())
                .collectList();
        carrierVoyageNumbers =
            voyageRepository
                .findCarrierVoyageNumbersByTransportDocumentRef(shipmentEvent.getDocumentID())
                .collectList();

        carrierServiceCodes =
            serviceRepository
                .findCarrierServiceCodesByTransportDocumentRef(shipmentEvent.getDocumentID())
                .collectList();

        transportDocumentReferences =
            transportDocumentRepository
                .findDistinctTransportDocumentReferencesByTransportDocumentReference(
                    shipmentEvent.getDocumentID())
                .map(TransportDocument::getTransportDocumentReference)
                .collectList();

        transportDocumentTypeCodes =
            transportDocumentTypeRepository
                .findCodesByTransportDocumentReference(shipmentEvent.getDocumentID())
                .collectList();

        transportCallIDs =
            transportCallRepository
                .findTransportCallIDByTransportDocumentRef(shipmentEvent.getDocumentID())
                .collectList();
        equipmentReferences =
            shipmentEquipmentRepository
                .findEquipmentReferenceByTransportDocumentRef(shipmentEvent.getDocumentID())
                .collectList();
        vesselIMONumbers =
            transportRepository
                .findVesselIMONumbersByTransportDocumentRef(shipmentEvent.getDocumentID())
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
                    shipmentEvent.getEventType(),
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
        Mono<List<String>> carrierVoyageNumbers = transportCallRepository
                .findCarrierVoyageNumbersByTransportCallID(transportEvent.getTransportCallID())
                .collectList();
        Mono<List<String>> carrierServiceCodes = transportCallRepository
                .findCarrierServiceCodesByTransportCallID(transportEvent.getTransportCallID())
                .collectList();

        TransportEventTypeCode transportEventTypeCode = transportEvent.getTransportEventTypeCode();
        String vesselIMONumber = transportEvent.getTransportCall().getVessel().getVesselIMONumber();
        String transportCallID = transportEvent.getTransportCallID();

        List<DocumentReferenceTO> documentReferences = transportEvent.getDocumentReferences();
        List<String> carrierBookingReferences = documentReferences.stream()
                .filter(documentReferenceTO -> documentReferenceTO.getDocumentReferenceType() == DocumentReferenceType.BKG)
                .map(DocumentReferenceTO::getDocumentReferenceValue)
                .collect(Collectors.toList());
        List<String> transportDocumentReferences = documentReferences.stream()
                .filter(documentReferenceTO -> documentReferenceTO.getDocumentReferenceType() == DocumentReferenceType.TRD)
                .map(DocumentReferenceTO::getDocumentReferenceValue)
                .collect(Collectors.toList());

        Mono<List<String>> transportDocumentTypeCodes = Flux.fromIterable(transportDocumentReferences)
                .flatMap(transportCallRepository::findTransportDocumentTypeCodeByTransportDocumentReference)
                .collectList();

        return Mono.zip(carrierVoyageNumbers, carrierServiceCodes, transportDocumentTypeCodes).flatMapMany(vnScTc -> {
            List<String> voyageNumbers = vnScTc.getT1();
            List<String> serviceCodes = vnScTc.getT2();
            List<String> documentTypeCodes = vnScTc.getT3();

            return eventSubscriptionRepository.findByTransportEventFields(
                    voyageNumbers, serviceCodes,
                    transportEventTypeCode, vesselIMONumber, transportCallID,
                    carrierBookingReferences, transportDocumentReferences,
                    documentTypeCodes,
                    transportEvent.getEventType()
            );
        });
    }
}
