package org.dcsa.core.events.edocumentation.service.impl;

import lombok.AllArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.BookingMapper;
import org.dcsa.core.events.edocumentation.model.mapper.CommodityMapper;
import org.dcsa.core.events.edocumentation.model.mapper.ShipmentMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.*;
import org.dcsa.core.events.edocumentation.repository.RequestedEquipmentRepository;
import org.dcsa.core.events.edocumentation.repository.ShipmentLocationRepository;
import org.dcsa.core.events.edocumentation.service.BookingService;
import org.dcsa.core.events.model.transferobjects.ReferenceTO;
import org.dcsa.core.events.repository.BookingRepository;
import org.dcsa.core.events.repository.CommodityRepository;
import org.dcsa.core.events.repository.ReferenceRepository;
import org.dcsa.core.events.repository.ValueAddedServiceRequestRepository;
import org.dcsa.core.events.service.DocumentPartyService;
import org.dcsa.skernel.service.LocationService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {

  private final BookingMapper bookingMapper;
  private final CommodityMapper commodityMapper;
  private final ShipmentMapper shipmentMapper;

  private final LocationService locationService;
  private final DocumentPartyService documentPartyService;

  private final BookingRepository bookingRepository;
  private final CommodityRepository commodityRepository;
  private final ValueAddedServiceRequestRepository valueAddedServiceRequestRepository;
  private final ReferenceRepository referenceRepository;
  private final RequestedEquipmentRepository requestedEquipmentRepository;
  private final ShipmentLocationRepository shipmentLocationRepository;

  @Override
  public Mono<BookingTO> fetchByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.empty();
    return bookingRepository
        .findById(bookingID)
        .flatMap(
            booking -> {
              BookingTO bookingTO = bookingMapper.bookingToDTO(booking);
              return Mono.when(
                      locationService
                          .fetchLocationByID(booking.getInvoicePayableAt())
                          .doOnNext(bookingTO::setInvoicePayableAt),
                      locationService
                          .fetchLocationByID(booking.getPlaceOfIssueID())
                          .doOnNext(bookingTO::setPlaceOfIssue),
                      fetchCommoditiesByBookingID(booking.getId())
                          .doOnNext(bookingTO::setCommodities),
                      fetchValueAddedServiceRequestsByBookingID(booking.getId())
                          .doOnNext(bookingTO::setValueAddedServiceRequests),
                      fetchReferencesByBookingID(booking.getId())
                          .doOnNext(bookingTO::setReferences),
                      fetchRequestedEquipmentsByBookingID(booking.getId())
                          .doOnNext(bookingTO::setRequestedEquipments),
                      documentPartyService
                          .fetchDocumentPartiesByBookingID(booking.getId())
                          .doOnNext(bookingTO::setDocumentParties),
                      fetchShipmentLocationsByBookingID(booking.getId())
                          .doOnNext(bookingTO::setShipmentLocations))
                  .thenReturn(bookingTO);
            });
  }

  Mono<List<CommodityTO>> fetchCommoditiesByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.just(Collections.emptyList());
    return commodityRepository
        .findByBookingID(bookingID)
        .map(commodityMapper::commodityToDTO)
        .collectList();
  }

  Mono<List<ValueAddedServiceRequestTO>> fetchValueAddedServiceRequestsByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.just(Collections.emptyList());
    return valueAddedServiceRequestRepository
        .findByBookingID(bookingID)
        .map(
            vasr -> {
              ValueAddedServiceRequestTO vTo = new ValueAddedServiceRequestTO();
              vTo.setValueAddedServiceCode(vasr.getValueAddedServiceCode());
              return vTo;
            })
        .collectList();
  }

  Mono<List<ReferenceTO>> fetchReferencesByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.just(Collections.emptyList());
    return referenceRepository
        .findByBookingID(bookingID)
        .map(
            r -> {
              ReferenceTO referenceTO = new ReferenceTO();
              referenceTO.setReferenceType(r.getReferenceType());
              referenceTO.setReferenceValue(r.getReferenceValue());
              return referenceTO;
            })
        .collectList();
  }

  Mono<List<RequestedEquipmentTO>> fetchRequestedEquipmentsByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.just(Collections.emptyList());
    return requestedEquipmentRepository
        .findByBookingID(bookingID)
        .map(
            re -> {
              RequestedEquipmentTO requestedEquipmentTO = new RequestedEquipmentTO();
              requestedEquipmentTO.setRequestedEquipmentUnits(re.getRequestedEquipmentUnits());
              requestedEquipmentTO.setRequestedEquipmentSizeType(
                  re.getRequestedEquipmentSizeType());
              requestedEquipmentTO.setShipperOwned(re.getIsShipperOwned());
              return requestedEquipmentTO;
            })
        .collectList();
  }

  Mono<List<ShipmentLocationTO>> fetchShipmentLocationsByBookingID(UUID bookingID) {
    if (bookingID == null) return Mono.empty();
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
                          shipmentLocationTO.setLocationTO(lTO);
                          return Mono.just(shipmentLocationTO);
                        }))
        .collectList();
  }
}
