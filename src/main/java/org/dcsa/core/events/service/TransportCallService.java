package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Reference;
import org.dcsa.core.events.model.Seal;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.model.enums.DCSATransportType;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface TransportCallService {

    Mono<List<Reference>> findReferencesForTransportCallID(UUID transportCallID);

    Mono<List<Seal>> findSealsForTransportCallIDAndEquipmentReference(UUID transportCallID, String equipmentReference);

    Mono<TransportCall> create(TransportCall transportCall);

  Mono<TransportCall> findTransportCall(String UNLocationCode, String facilitySMDGCode,
                                        DCSATransportType modeOfTransport,
                                        String vesselIMONumber, String carrierServiceCode,
                                        String importVoyageNumber, String exportVoyageNumber,
                                        Integer transportCallSequenceNumber);
}
