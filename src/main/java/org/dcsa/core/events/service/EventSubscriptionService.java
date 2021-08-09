package org.dcsa.core.events.service;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.service.ExtendedBaseService;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EventSubscriptionService extends ExtendedBaseService<EventSubscription, UUID> {

  Flux<EventSubscription> findSubscriptionsFor(Event event);
}
