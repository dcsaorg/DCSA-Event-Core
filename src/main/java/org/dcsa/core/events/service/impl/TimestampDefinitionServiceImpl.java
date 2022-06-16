package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.TimestampDefinition;
import org.dcsa.core.events.model.enums.OperationsEventTypeCode;
import org.dcsa.core.events.model.enums.PortCallServiceTypeCode;
import org.dcsa.core.events.repository.TimestampDefinitionRepository;
import org.dcsa.core.events.service.TimestampDefinitionService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.dcsa.core.service.impl.QueryServiceImpl;
import org.dcsa.skernel.model.enums.PartyFunction;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class TimestampDefinitionServiceImpl extends QueryServiceImpl<TimestampDefinitionRepository, TimestampDefinition, String> implements TimestampDefinitionService {

  /** Helper for {@link #arePublisherRolesInterchangeable(PartyFunction, PartyFunction)} */
  private static final Map<PartyFunction, PartyFunction> NORMALIZED_PARTY_FUNCTION_MAP = Map.of(
    PartyFunction.AG, PartyFunction.CA,
    PartyFunction.VSL, PartyFunction.CA
  );

    private final TimestampDefinitionRepository timestampDefinitionRepository;

    @Override
    public TimestampDefinitionRepository getRepository() {
        return timestampDefinitionRepository;
    }

    @Override
    public Mono<OperationsEvent> markOperationsEventAsTimestamp(OperationsEvent operationsEvent) {
        return timestampDefinitionRepository.findAllByEventClassifierCodeAndOperationsEventTypeCodeAndPortCallPhaseTypeCodeAndPortCallServiceTypeCodeAndFacilityTypeCode(
                operationsEvent.getEventClassifierCode(),
                operationsEvent.getOperationsEventTypeCode(),
                operationsEvent.getPortCallPhaseTypeCode(),
                operationsEvent.getPortCallServiceTypeCode(),
                operationsEvent.getFacilityTypeCode()
        )
          .filter(definition -> isCorrectTimestampsForEvent(definition, operationsEvent))
          .collectList()
          .flatMap(timestampDefinitions -> {
            if (timestampDefinitions.isEmpty()) {
              return Mono.error(ConcreteRequestErrorMessageException.invalidInput("Cannot determine timestamp type for provided timestamp - please verify publisherRole, eventClassifierCode, facilityTypeCode, portCallPhaseTypeCode, and portCallServiceTypeCode"));
            }
            if (timestampDefinitions.size() >= 2) {
              return Mono.error(ConcreteRequestErrorMessageException.internalServerError("There should exactly one timestamp matching this input but we got two!"));
            }
            return Mono.just(timestampDefinitions.get(0));
          })
          .flatMap(timestampDefinition -> timestampDefinitionRepository.markOperationsEventAsTimestamp(operationsEvent.getEventID(), timestampDefinition.getId()))
          .thenReturn(operationsEvent);
    }

  /**
   * Detect mismatching timestamp definitions
   *
   * Ideally, all timestamp definitions would be distinct without resorting to these tricks. Unfortunately,
   * we have "Terminal ready for vessel departure" and "Vessel ready to sail", which can basically only be told
   * apart based on publisher role (or mode of transport, but we have normalized that as OperationEvents require
   * that field to be "not null").
   *
   * This Predicate-like method is here to prune obvious mismatches
   */
  private boolean isCorrectTimestampsForEvent(TimestampDefinition definition, OperationsEvent operationsEvent) {
    // Since it is gross hack to rely on publisherRole, lets limit it to only the JIT 1.1 version of these problematic timestamps
    // - they are the only ones that are "SAFE" + "DEPA"
    if (operationsEvent.getPortCallServiceTypeCode() == PortCallServiceTypeCode.SAFE && operationsEvent.getOperationsEventTypeCode() == OperationsEventTypeCode.DEPA) {
      return arePublisherRolesInterchangeable(definition.getPublisherRole(), operationsEvent.getPublisherRole());
    }
    return true;
  }

  /**
   * Determine if two party functions (publisherRoles) are interchangeable
   *
   * In JIT, most of the carrier published timestamps can freely choose one of the CA, AG or VSL publisherRole
   * even though the IFS denotes a concrete one as the example.
   *
   * @return true if the two party functions are either identical or both of them are one of CA, AG or VSL.
   */
  private static boolean arePublisherRolesInterchangeable(PartyFunction lhs, PartyFunction rhs) {
    PartyFunction lhsNormalized = NORMALIZED_PARTY_FUNCTION_MAP.getOrDefault(lhs, lhs);
    PartyFunction rhsNormalized = NORMALIZED_PARTY_FUNCTION_MAP.getOrDefault(rhs, rhs);
    return lhsNormalized == rhsNormalized;
  }

    @Override
    public Flux<TimestampDefinition> findAll() {
        return timestampDefinitionRepository.findAll();
    }
}
