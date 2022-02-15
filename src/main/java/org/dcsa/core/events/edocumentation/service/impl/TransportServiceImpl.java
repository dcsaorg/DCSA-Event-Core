package org.dcsa.core.events.edocumentation.service.impl;

import lombok.AllArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.TransportMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.TransportTO;
import org.dcsa.core.events.edocumentation.service.TransportService;
import org.dcsa.core.events.model.*;
import org.dcsa.core.events.model.enums.EventClassifierCode;
import org.dcsa.core.events.model.enums.TransportEventTypeCode;
import org.dcsa.core.events.model.transferobjects.LocationTO;
import org.dcsa.core.events.repository.*;
import org.dcsa.core.events.service.LocationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
@Service
public class TransportServiceImpl implements TransportService {

  private final TransportMapper transportMapper;

  private final TransportRepository transportRepository;
  private final TransportEventRepository transportEventRepository;
  private final TransportCallRepository transportCallRepository;
  private final ModeOfTransportRepository modeOfTransportRepository;
  private final VesselRepository vesselRepository;
  private final VoyageRepository voyageRepository;

  private final LocationService locationService;

  @Override
  public Flux<TransportTO> findByTransportID(UUID transportID) {
    if (transportID == null) return Flux.empty();
    return transportRepository
        .findAllById(List.of(transportID))
        .flatMap(
            transport ->
                fetchTransportCallByID(transport.getLoadTransportCallID())
                    .flatMap(
                        tc -> {
                          TransportTO transportTO = transportMapper.transportToDTO(transport);
                          transportTO.setTransportName(transport.getTransportName());
                          transportTO.setTransportReference(transport.getTransportReference());

                          return Mono.when(
                                  fetchTransportEventByTransportID(transport.getTransportID())
                                      .doOnNext(
                                          t2 -> {
                                            transportTO.setPlannedDepartureDate(
                                                t2.getT1().getEventDateTime());
                                            transportTO.setPlannedArrivalDate(
                                                t2.getT2().getEventDateTime());
                                          }),
                                  fetchLocationByTransportCallID(transport.getLoadTransportCallID())
                                      .doOnNext(transportTO::setLoadLocation),
                                  fetchLocationByTransportCallID(
                                          transport.getDischargeTransportCallID())
                                      .doOnNext(transportTO::setLoadLocation),
                                  fetchModeOfTransportByTransportCallID(
                                          transport.getLoadTransportCallID())
                                      .doOnNext(
                                          mod ->
                                              transportTO.setModeOfTransport(
                                                  mod.getDcsaTransportType())),
                                  fetchVesselByTransportCallID(tc.getTransportCallID())
                                      .doOnNext(
                                          v -> {
                                            transportTO.setVesselName(v.getVesselName());
                                            transportTO.setVesselIMONumber(v.getVesselIMONumber());
                                          }),
                                  fetchImportExportVoyageNumberByTransportCallID(tc)
                                      .doOnNext(
                                          cvnMap -> {
                                            transportTO.setImportVoyageNumber(
                                                cvnMap.get("importVoyageNumber"));
                                            transportTO.setExportVoyageNumber(
                                                cvnMap.get("exportVoyageNumber"));
                                          }))
                              .thenReturn(transportTO);
                        }));
  }

  Mono<Tuple2<TransportEvent, TransportEvent>> fetchTransportEventByTransportID(UUID transportId) {
    return Mono.justOrEmpty(transportId)
        .flatMap(
            tID ->
                transportRepository
                    .findById(transportId)
                    .flatMap(
                        x ->
                            Mono.zip(
                                transportEventRepository
                                    .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
                                        x.getLoadTransportCallID(),
                                        TransportEventTypeCode.ARRI,
                                        EventClassifierCode.PLN),
                                transportEventRepository
                                    .findFirstByTransportCallIDAndEventTypeCodeAndEventClassifierCodeOrderByEventDateTimeDesc(
                                        x.getDischargeTransportCallID(),
                                        TransportEventTypeCode.DEPA,
                                        EventClassifierCode.PLN))));
  }

  Mono<TransportCall> fetchTransportCallByID(String transportCallID) {
    return Mono.justOrEmpty(transportCallID).flatMap(transportCallRepository::findById);
  }

  Mono<LocationTO> fetchLocationByTransportCallID(String id) {
    return Mono.justOrEmpty(id)
        .flatMap(this::fetchTransportCallByID)
        .flatMap(transportCall -> locationService.fetchLocationByID(transportCall.getLocationID()));
  }

  Mono<ModeOfTransport> fetchModeOfTransportByTransportCallID(String transportCallId) {
    return Mono.justOrEmpty(transportCallId)
        .flatMap(modeOfTransportRepository::findByTransportCallID);
  }

  Mono<Vessel> fetchVesselByTransportCallID(String transportCallId) {
    return Mono.justOrEmpty(transportCallId)
        .flatMap(this::fetchTransportCallByID)
        .flatMap(
            x -> {
              if (x.getVesselID() == null) {
                return Mono.empty();
              }
              return vesselRepository.findById(x.getVesselID());
            });
  }

  Mono<Map<String, String>> fetchImportExportVoyageNumberByTransportCallID(
      TransportCall transportCall) {
    if (transportCall == null) return Mono.just(Collections.emptyMap());
    if (transportCall.getImportVoyageID() == null) return Mono.just(Collections.emptyMap());

    return voyageRepository
        .findById(transportCall.getImportVoyageID())
        .flatMap(
            voyage -> {
              Mono<Voyage> exportVoyage;
              if (transportCall.getExportVoyageID() != null
                  && !transportCall.getExportVoyageID().equals(transportCall.getImportVoyageID())) {
                exportVoyage = voyageRepository.findById(transportCall.getExportVoyageID());
              } else {
                exportVoyage = Mono.just(voyage);
              }
              return Mono.zip(Mono.just(voyage), exportVoyage);
            })
        .map(
            voyages ->
                Map.of(
                    "importVoyageNumber",
                    voyages.getT1().getCarrierVoyageNumber(),
                    "exportVoyageNumber",
                    voyages.getT2().getCarrierVoyageNumber()));
  }
}
