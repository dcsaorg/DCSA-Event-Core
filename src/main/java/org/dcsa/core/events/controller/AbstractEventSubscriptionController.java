package org.dcsa.core.events.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.core.events.service.EventSubscriptionTOService;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;
import org.dcsa.core.exception.DCSAException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(
    value = "event-subscriptions",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public abstract class AbstractEventSubscriptionController<
        S extends EventSubscriptionTOService<T>, T extends AbstractEventSubscription> {

  private final ExtendedParameters extendedParameters;

  private final R2dbcDialect r2dbcDialect;

  protected abstract S getService();

  protected ExtendedRequest<EventSubscription> newExtendedRequest() {
    return new ExtendedRequest<>(extendedParameters, r2dbcDialect, EventSubscription.class);
  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<T> findById(@PathVariable UUID id) {
    return getService().findById(id);
  }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<T> create(@Valid @RequestBody T t) {
        S s = getService();
        if (t.getSubscriptionID() != null) {
            return Mono.error(ConcreteRequestErrorMessageException.invalidParameter("EventSubscription", null,
                    "Id not allowed when creating a new EventSubscription"));
        }
        return s.create(t);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<T> update(@PathVariable UUID id, @Valid @RequestBody T t) {
        S s = getService();
        if (!id.equals(t.getSubscriptionID())) {
            return Mono.error(ConcreteRequestErrorMessageException.invalidParameter("EventSubscription", null,
                    "Id in url does not match id in body"));
        }
        return s.update(t);
    }

  @GetMapping
  public Flux<T> findAll(ServerHttpResponse response, ServerHttpRequest request) {
    ExtendedRequest<EventSubscription> extendedRequest = newExtendedRequest();
    try {
      extendedRequest.parseParameter(request.getQueryParams());
    } catch (DCSAException ex) {
      return Flux.error(ex);
    }

    return getService()
        .findAllExtended(extendedRequest)
        .filter(e ->
                (e.getEventType().stream().filter(et -> !getService().getAllowedEventTypes().contains(et)).count() == 0))
        .doOnComplete(
            () -> {
              // Add Link headers to the response
              extendedRequest.insertHeaders(response, request);
            });
  }

  @PutMapping("{id}/secret")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> updateSecret(
      @PathVariable UUID id,
      @Valid @RequestBody EventSubscriptionSecretUpdateTO eventSubscriptionSecretUpdateTO) {
    return getService().updateSecret(id, eventSubscriptionSecretUpdateTO);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return getService().deleteById(id);
  }
}
