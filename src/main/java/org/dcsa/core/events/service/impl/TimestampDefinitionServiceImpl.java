package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.events.repository.TimestampDefinitionRepository;
import org.dcsa.core.events.service.TimestampDefinitionService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.QueryServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class TimestampDefinitionServiceImpl extends QueryServiceImpl<TimestampDefinitionRepository, TimestampDefinition, String> implements TimestampDefinitionService {

    private final TimestampDefinitionRepository timestampDefinitionRepository;

    @Override
    public TimestampDefinitionRepository getRepository() {
        return timestampDefinitionRepository;
    }

    @Override
    public Mono<OperationsEvent> markOperationsEventAsTimestamp(OperationsEvent operationsEvent) {
        return timestampDefinitionRepository.findByPublisherRoleAndEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                operationsEvent.getPublisherRole(),
                operationsEvent.getEventClassifierCode(),
                operationsEvent.getOperationsEventTypeCode(),
                operationsEvent.getPortCallPhaseTypeCode(),
                operationsEvent.getPortCallServiceTypeCode(),
                operationsEvent.getFacilityTypeCode()
        )
                .flatMap(timestampDefinition -> {
                    if (timestampDefinition.getCanonicalTimestampDefinition() != null) {
                        return timestampDefinitionRepository.findById(timestampDefinition.getCanonicalTimestampDefinition());
                    }
                    return Mono.just(timestampDefinition);
                })
                .switchIfEmpty(Mono.error(new CreateException("Cannot determine timestamp type for provided timestamp - please verify publisherRole, eventClassifierCode, facilityTypeCode, portCallPhaseTypeCode, and portCallServiceTypeCode")))
                .flatMap(timestampDefinition -> timestampDefinitionRepository.markOperationsEventAsTimestamp(operationsEvent.getEventID(), timestampDefinition.getId()))
                .thenReturn(operationsEvent);
    }

    @Override
    public Flux<TimestampDefinition> findAll() {
        return timestampDefinitionRepository.findAll();
    }
}
