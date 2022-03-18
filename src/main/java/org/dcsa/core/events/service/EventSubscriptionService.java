package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.service.QueryService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface EventSubscriptionService extends QueryService<EventSubscription, UUID> {

    Mono<EventSubscription> findById(UUID id);

    Mono<EventSubscription> create(EventSubscription eventSubscription);

    Flux<EventSubscription> findSubscriptionsFor(Event event);

    Mono<Void> deleteById(UUID subscriptionID);

    Mono<EventSubscription> update(EventSubscription t);
}
