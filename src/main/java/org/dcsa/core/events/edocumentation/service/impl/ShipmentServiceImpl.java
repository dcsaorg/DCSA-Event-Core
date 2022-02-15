package org.dcsa.core.events.edocumentation.service.impl;

import lombok.AllArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.CarrierClauseMapper;
import org.dcsa.core.events.edocumentation.model.mapper.ChargeMapper;
import org.dcsa.core.events.edocumentation.model.mapper.ConfirmedEquipmentMapper;
import org.dcsa.core.events.edocumentation.model.mapper.ShipmentMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.*;
import org.dcsa.core.events.edocumentation.repository.*;
import org.dcsa.core.events.edocumentation.service.BookingService;
import org.dcsa.core.events.edocumentation.service.ShipmentService;
import org.dcsa.core.events.edocumentation.service.TransportService;
import org.dcsa.core.events.repository.ShipmentRepository;
import org.dcsa.core.events.service.LocationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
class ShipmentServiceImpl implements ShipmentService {

  // mappers
  private final ShipmentMapper shipmentMapper;
  private final CarrierClauseMapper carrierClauseMapper;
  private final ConfirmedEquipmentMapper confirmedEquipmentMapper;
  private final ChargeMapper chargeMapper;

  // services
  private final LocationService locationService;
  private final TransportService transportService;
  private final BookingService bookingService;

  // repos
  private final ShipmentRepository shipmentRepository;
  private final ShipmentCutOffTimeRepository shipmentCutOffTimeRepository;
  private final ShipmentLocationRepository shipmentLocationRepository;
  private final ShipmentCarrierClausesRepository shipmentCarrierClausesRepository;
  private final CarrierClauseRepository carrierClauseRepository;
  private final RequestedEquipmentRepository requestedEquipmentRepository;
  private final ChargeRepository chargeRepository;
  private final ShipmentTransportRepository shipmentTransportRepository;

  @Override
  public Flux<ShipmentTO> findByShippingInstructionID(String id) {
    return shipmentRepository
        .findByShippingInstructionID(id)
        .map(s -> Tuples.of(s, shipmentMapper.shipmentToDTO(s)))
        .flatMap(
            t -> {
              ShipmentTO shipmentTO = t.getT2();
              return Mono.when(
                      fetchShipmentCutOffTimeByShipmentID(t.getT1().getShipmentID())
                          .doOnNext(shipmentTO::setShipmentCutOffTimes),
                      fetchShipmentLocationsByBookingID(t.getT1().getBookingID())
                          .doOnNext(shipmentTO::setShipmentLocations),
                      fetchCarrierClausesByShipmentID(t.getT1().getShipmentID())
                          .doOnNext(shipmentTO::setCarrierClauses),
                      fetchConfirmedEquipmentByByBookingID(t.getT1().getBookingID())
                          .doOnNext(shipmentTO::setConfirmedEquipments),
                      fetchChargesByShipmentID(t.getT1().getShipmentID())
                          .doOnNext(shipmentTO::setCharges),
                      bookingService
                          .fetchByBookingID(t.getT1().getBookingID())
                          .doOnNext(shipmentTO::setBooking),
                      fetchTransportsByShipmentID(t.getT1().getShipmentID())
                          .doOnNext(shipmentTO::setTransports))
                  .thenReturn(shipmentTO);
            });
  }

  Mono<List<ShipmentCutOffTimeTO>> fetchShipmentCutOffTimeByShipmentID(UUID shipmentID) {
    if (shipmentID == null) return Mono.just(Collections.emptyList());
    return shipmentCutOffTimeRepository
        .findAllByShipmentID(shipmentID)
        .map(shipmentMapper::shipmentCutOffTimeToDTO)
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  Mono<List<ShipmentLocationTO>> fetchShipmentLocationsByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.just(Collections.emptyList());
    return shipmentLocationRepository
        .findByBookingID(bookingID)
        .flatMap(
            sl ->
                locationService
                    .fetchLocationByID(sl.getLocationID())
                    .flatMap(
                        lTO -> {
                          ShipmentLocationTO shipmentLocationTO =
                              shipmentMapper.shipmentLocationToDTO(sl);
                          shipmentLocationTO.setLocation(lTO);
                          return Mono.just(shipmentLocationTO);
                        }))
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  Mono<List<CarrierClauseTO>> fetchCarrierClausesByShipmentID(UUID shipmentID) {
    if (shipmentID == null) return Mono.just(Collections.emptyList());
    return shipmentCarrierClausesRepository
        .findAllByShipmentID(shipmentID)
        .flatMap(
            shipmentCarrierClause ->
                carrierClauseRepository.findById(shipmentCarrierClause.getCarrierClauseID()))
        .flatMap(x -> Mono.just(carrierClauseMapper.carrierClauseToDTO(x)))
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  Mono<List<ConfirmedEquipmentTO>> fetchConfirmedEquipmentByByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.just(Collections.emptyList());
    return requestedEquipmentRepository
        .findByBookingID(bookingID)
        .map(confirmedEquipmentMapper::requestedEquipmentToDto)
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  Mono<List<ChargeTO>> fetchChargesByShipmentID(UUID shipmentID) {
    if (shipmentID == null) return Mono.just(Collections.emptyList());
    return chargeRepository
        .findAllByShipmentID(shipmentID)
        .map(chargeMapper::chargeToDTO)
        .collectList()
        .defaultIfEmpty(Collections.emptyList());
  }

  Mono<List<TransportTO>> fetchTransportsByShipmentID(UUID shipmentID) {
    if (shipmentID == null) return Mono.just(Collections.emptyList());
    return shipmentTransportRepository
        .findAllByShipmentID(shipmentID)
        .flatMap(
            st ->
                transportService
                    .findByTransportID(st.getTransportID())
                    .map(
                        tTO -> {
                          tTO.setTransportPlanStage(st.getTransportPlanStageCode());
                          tTO.setTransportPlanStageSequenceNumber(
                              st.getTransportPlanStageSequenceNumber());
                          tTO.setIsUnderShippersResponsibility(
                              st.getIsUnderShippersResponsibility());
                          return tTO;
                        }))
        .collectList();
  }
}
