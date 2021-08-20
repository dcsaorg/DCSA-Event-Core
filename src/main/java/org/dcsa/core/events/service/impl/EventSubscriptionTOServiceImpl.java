package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.events.service.EventSubscriptionTOService;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.impl.BaseServiceImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EventSubscriptionTOServiceImpl<
        T extends AbstractEventSubscription, S extends EventSubscriptionService>
    extends BaseServiceImpl<T, UUID> implements EventSubscriptionTOService<T> {

  protected abstract S getService();

  protected abstract Flux<T> mapManyD2TO(Flux<EventSubscription> eventSubscriptionFlux);

  protected abstract Mono<T> mapSingleD2TO(Mono<EventSubscription> eventSubscriptionMono);

  @Override
  public Flux<T> findAll() {
    return mapManyD2TO(getService().findAll());
  }

  @Override
  public Mono<T> findById(UUID id) {
    return mapSingleD2TO(getService().findById(id));
  }

  @Override
  public Flux<T> findAllExtended(ExtendedRequest<EventSubscription> extendedRequest) {
    return mapManyD2TO(getService().findAll());
  }

  @Override
  public Mono<Void> updateSecret(
      UUID subscriptionID, EventSubscriptionSecretUpdateTO eventSubscriptionSecretUpdateTO) {
    return getService()
        .findById(subscriptionID)
        .doOnNext(
            eventSubscription ->
                eventSubscription.setSecret(eventSubscriptionSecretUpdateTO.getSecret()))
        .flatMap(getService()::update)
        .then();
  }

  @Override
  public Mono<Void> deleteById(UUID id) {
    return getService().deleteById(id);
  }

  @Override
  public Mono<Void> delete(T eventSubscriptionTO) {
    return getService().deleteById(eventSubscriptionTO.getSubscriptionID());
  }

  @Override
  public UUID getIdOfEntity(T entity) {
    return entity.getSubscriptionID();
  }

  protected final Function<String, List<EventType>> stringToEventTypeList =
      s -> {
        if (s.contains(",")) {
          return Arrays.stream(s.split(",")).map(EventType::valueOf).collect(Collectors.toList());
        } else {
          return Stream.of(s).map(EventType::valueOf).collect(Collectors.toList());
        }
      };
}
