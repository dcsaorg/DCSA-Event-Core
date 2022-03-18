package org.dcsa.core.events.service;

import org.dcsa.core.events.model.enums.CarrierCodeListProvider;
import org.dcsa.core.events.model.Carrier;
import org.dcsa.core.service.QueryService;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CarrierService extends QueryService<Carrier, UUID> {

    Mono<Carrier> findByCode(CarrierCodeListProvider carrierCodeListProvider, String carrierCode);
}
