package org.dcsa.core.events.service;

import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.BaseService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventSubscriptionTOService<T> extends BaseService<T, UUID> {
  Flux<T> findAllExtended(ExtendedRequest<EventSubscription> extendedRequest);

  Mono<Void> updateSecret(
      UUID subscriptionID, EventSubscriptionSecretUpdateTO eventSubscriptionSecretUpdateTO);
}