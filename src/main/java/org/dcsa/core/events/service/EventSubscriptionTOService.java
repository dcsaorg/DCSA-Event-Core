package org.dcsa.core.events.service;

import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.enums.EventType;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface EventSubscriptionTOService<T> {
  Flux<T> findAll();

  Mono<T> findById(UUID id);

  Flux<T> findAllExtended(ExtendedRequest<EventSubscription> extendedRequest);

  Mono<Void> updateSecret(
      UUID subscriptionID, EventSubscriptionSecretUpdateTO eventSubscriptionSecretUpdateTO);

  List<EventType> getAllowedEventTypes();

  Mono<Void> deleteById(UUID id);

  Mono<T> create(T t);

  Mono<T> update(T t);
}
