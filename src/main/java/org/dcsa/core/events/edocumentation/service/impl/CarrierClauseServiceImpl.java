package org.dcsa.core.events.edocumentation.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.CarrierClauseMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.CarrierClauseTO;
import org.dcsa.core.events.edocumentation.repository.CarrierClauseRepository;
import org.dcsa.core.events.edocumentation.service.CarrierClauseService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CarrierClauseServiceImpl implements CarrierClauseService {
  private final CarrierClauseRepository carrierClauseRepository;
  private final CarrierClauseMapper carrierClauseMapper;

  @Override
  public Flux<CarrierClauseTO> fetchCarrierClausesByTransportDocumentID(
      UUID transportDocumentId) {
    return carrierClauseRepository
        .fetchAllByTransportDocumentID(transportDocumentId)
        .map(carrierClauseMapper::carrierClauseToDTO);
  }

  @Override
  public Flux<CarrierClauseTO> fetchCarrierClausesByShipmentID(UUID shipmentID) {
    return carrierClauseRepository
        .fetchAllByShipmentID(shipmentID)
        .map(carrierClauseMapper::carrierClauseToDTO);
  }
}
