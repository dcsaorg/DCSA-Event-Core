package org.dcsa.core.events.edocumentation.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.ChargeMapper;
import org.dcsa.core.events.edocumentation.model.transferobject.ChargeTO;
import org.dcsa.core.events.edocumentation.repository.ChargeRepository;
import org.dcsa.core.events.edocumentation.service.ChargeService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class ChargeServiceImpl implements ChargeService {

  private final ChargeRepository chargeRepository;
  private final ChargeMapper chargeMapper;

  @Override
  public Flux<ChargeTO> fetchChargesByTransportDocumentID(UUID transportDocumentID) {
    return chargeRepository
        .findAllByTransportDocumentID(transportDocumentID)
        .map(chargeMapper::chargeToDTO);
  }

  @Override
  public Flux<ChargeTO> fetchChargesByShipmentID(UUID shipmentID) {
    return chargeRepository.findAllByShipmentID(shipmentID).map(chargeMapper::chargeToDTO);
  }
}
