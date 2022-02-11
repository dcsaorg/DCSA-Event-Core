package org.dcsa.core.events.service;

import org.dcsa.core.events.model.enums.CarrierCodeListProvider;
import org.dcsa.core.service.ExtendedBaseService;
import org.dcsa.core.events.model.Carrier;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CarrierService {

    Mono<Carrier> findByCode(CarrierCodeListProvider carrierCodeListProvider, String carrierCode);
}
