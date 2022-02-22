package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Carrier;
import org.dcsa.core.events.model.enums.CarrierCodeListProvider;
import org.dcsa.core.events.repository.CarrierRepository;
import org.dcsa.core.events.service.CarrierService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.QueryServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class CarrierServiceImpl extends QueryServiceImpl<CarrierRepository, Carrier, UUID> implements CarrierService {

    private final CarrierRepository carrierRepository;

    @Override
    protected CarrierRepository getRepository() {
        return carrierRepository;
    }

    @Override
    public Mono<Carrier> findByCode(CarrierCodeListProvider carrierCodeListProvider, String carrierCode) {
        Function<String, Mono<Carrier>> method;
        switch (Objects.requireNonNull(carrierCodeListProvider, "carrierCodeListProvider")) {
            case SMDG:
                method = carrierRepository::findBySmdgCode;
                break;
            case NMFTA:
                method = carrierRepository::findByNmftaCode;
                break;
            default:
                throw new CreateException("Unsupported vessel operator carrier code list provider: " + carrierCodeListProvider);
        }
        return method.apply(Objects.requireNonNull(carrierCode, "carrierCode"))
                .switchIfEmpty(Mono.error(new CreateException("Cannot find any vessel operator with carrier code: "
                        + carrierCode )));
    }
}
