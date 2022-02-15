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
import reactor.util.function.Tuples;

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
  public Flux<TransportTO> findByTransportID(UUID id) {
    return transportRepository
        .findAllById(List.of(id))
        .flatMap(
            transport ->
                fetchTransportCallById(transport.getLoadTransportCallID())
                    .flatMap(
                        tc -> {
                          TransportTO transportTO = transportMapper.transportToDTO(transport);
                          transportTO.setTransportName(transport.getTransportName());
                          transportTO.setTransportReference(transport.getTransportReference());

                          return Mono.when(
                                  fetchTransportEventByTransportId(transport.getTransportID())
                                      .doOnNext(
                                          t2 -> {
                                            transportTO.setPlannedDepartureDate(
                                                t2.getT1().getEventDateTime());
                                            transportTO.setPlannedArrivalDate(
                                                t2.getT2().getEventDateTime());
                                          }),
                                  fetchLocationByTransportCallId(transport.getLoadTransportCallID())
                                      .doOnNext(transportTO::setLoadLocation),
                                  fetchLocationByTransportCallId(
                                          transport.getDischargeTransportCallID())
                                      .doOnNext(transportTO::setLoadLocation),
                                  fetchModeOfTransportByTransportCallId(
                                          transport.getLoadTransportCallID())
                                      .doOnNext(
                                          mod ->
                                              transportTO.setModeOfTransport(
                                                  mod.getDcsaTransportType())),
                                  fetchVesselByTransportCallId(tc.getTransportCallID())
                                      .doOnNext(
                                          v -> {
                                            transportTO.setVesselName(v.getVesselName());
                                            transportTO.setVesselIMONumber(v.getVesselIMONumber());
                                          }),
                                  fetchImportExportVoyageNumberByTransportCallId(tc)
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

  Mono<Tuple2<TransportEvent, TransportEvent>> fetchTransportEventByTransportId(UUID transportId) {
    return transportRepository
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
                                EventClassifierCode.PLN))
                    .flatMap(y -> Mono.just(Tuples.of(y.getT1(), y.getT2()))));
  }

  private Mono<TransportCall> fetchTransportCallById(String transportCallId) {
    if (transportCallId == null) return Mono.empty();
    return transportCallRepository.findById(transportCallId);
  }

  Mono<LocationTO> fetchLocationByTransportCallId(String id) {
    if (id == null) return Mono.empty();
    return fetchTransportCallById(id)
        .flatMap(transportCall -> locationService.fetchLocationByID(transportCall.getLocationID()));
  }

  Mono<ModeOfTransport> fetchModeOfTransportByTransportCallId(String transportCallId) {
    if (transportCallId == null) return Mono.empty();
    return modeOfTransportRepository.findByTransportCallID(transportCallId);
  }

  Mono<Vessel> fetchVesselByTransportCallId(String transportCallId) {

    if (transportCallId == null) return Mono.empty();
    return fetchTransportCallById(transportCallId)
        .flatMap(
            x -> {
              if (x.getVesselID() == null) {
                return Mono.empty();
              }
              return vesselRepository.findById(x.getVesselID());
            });
  }

  Mono<Map<String, String>> fetchImportExportVoyageNumberByTransportCallId(
      TransportCall transportCall) {
    if (transportCall == null) return Mono.just(Collections.emptyMap());
    if (transportCall.getImportVoyageID() == null) return Mono.just(Collections.emptyMap());

    return voyageRepository
        .findById(transportCall.getImportVoyageID())
        .flatMap(
            voyage -> {
              Mono<Voyage> exportVoyage;
              if (!transportCall.getExportVoyageID().equals(transportCall.getImportVoyageID())) {
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
