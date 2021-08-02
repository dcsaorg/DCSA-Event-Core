package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.OperationsEvent;
import org.dcsa.core.events.model.base.AbstractOperationsEvent;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.transferobjects.OperationsEventTO;
import org.dcsa.core.events.repository.OperationsEventRepository;
import org.dcsa.core.events.service.OperationsEventService;
import org.dcsa.core.events.service.OperationsEventTOService;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class OperationsEventTOServiceImpl extends BaseServiceImpl<OperationsEventTO, UUID> implements OperationsEventTOService {

    private static final List<EventType> ALL_EVENT_TYPES = List.of(EventType.values());

    private final OperationsEventService operationsEventService;
    private final OperationsEventRepository operationsEventRepository;


    @Override
    public Flux<OperationsEventTO> findAllExtended(ExtendedRequest<AbstractOperationsEvent> extendedRequest) {
        return null;
    }

    @Override
    public Flux<OperationsEventTO> findAll() {
        return null;
    }

    @Override
    public Mono<OperationsEventTO> findById(UUID id) {
        return null;
    }

    @Override
    public Mono<OperationsEventTO> create(OperationsEventTO operationsEventTO) {
        return null;
    }

    @Override
    public Mono<OperationsEventTO> update(OperationsEventTO operationsEventTO) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(UUID id) {
        return null;
    }

    @Override
    public Mono<Void> delete(OperationsEventTO operationsEventTO) {
        return null;
    }

    @Override
    public UUID getIdOfEntity(OperationsEventTO entity) {
        return null;
    }


    private Flux<OperationsEventTO> mapDaoToTO(Flux<OperationsEvent> operationsEventFlux){

        return null;

    }
        /*  return operationsEventFlux.map(operationsEvent -> MappingUtils.instanceFrom(operationsEvent, OperationsEventTO::new, OperationsEvent.class))
                .collectList()
                .flatMapMany(operationsEventsList -> {
                    Map<UUID, OperationsEventTO> id2operations = operationsEventsList.stream().collect(Collectors.toMap(OperationsEvent::getId,
                            Function.identity()

                    ));
        return Flux.fromIterable(operationsEventsList)
                .doOnNext(eventTO -> eventTO.setTransportCallId(new String()))
                .map(OperationsEvent::getTransportCall::get)
    }
*/

}
