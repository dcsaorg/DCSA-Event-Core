package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.dcsa.core.events.repository.TimestampDefinitionRepository;
import org.dcsa.core.events.service.TimestampDefinitionService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class TimestampDefinitionServiceImpl extends ExtendedBaseServiceImpl<TimestampDefinitionRepository, TimestampDefinition, String> implements TimestampDefinitionService {

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
                .switchIfEmpty(Mono.error(new CreateException("Cannot determine timestamp type for provided timestamp - please verify publisherRole, eventClassifierCode, facilityTypeCode, portCallPhaseTypeCode, and portCallServiceTypeCode")))
                .flatMap(timestampDefinition -> timestampDefinitionRepository.markOperationsEventAsTimestamp(operationsEvent.getEventID(), timestampDefinition.getId()))
                .thenReturn(operationsEvent);
    }

}
