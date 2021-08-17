package org.dcsa.core.events.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.core.events.service.EventSubscriptionTOService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.exception.UpdateException;
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
import java.util.Objects;
import java.util.UUID;

@RequestMapping(
    value = "event-subscriptions",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public abstract class AbstractEventSubscriptionController<
        S extends EventSubscriptionTOService<T>, T extends AbstractEventSubscription>
    extends BaseController<S, T, UUID> {

  private final ExtendedParameters extendedParameters;

  private final R2dbcDialect r2dbcDialect;

  protected ExtendedRequest<T> newExtendedRequest(Class<T> clazz) {
    return new ExtendedRequest<>(extendedParameters, r2dbcDialect, clazz);
  }

  @Override
  public String getType() {
    return "EventSubscription";
  }

  @GetMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<T> findById(@PathVariable UUID id) {
    return getService().findById(id);
  }

  @GetMapping
  public Flux<T> findAll(ServerHttpResponse response, ServerHttpRequest request) {
    ExtendedRequest<EventSubscription> extendedRequest =
        new ExtendedRequest<>(extendedParameters, r2dbcDialect, EventSubscription.class);
    try {
      extendedRequest.parseParameter(request.getQueryParams());
    } catch (GetException getException) {
      return Flux.error(getException);
    }

    return getService()
        .findAllExtended(extendedRequest)
        .doOnComplete(
            () -> {
              // Add Link headers to the response
              extendedRequest.insertHeaders(response, request);
            });
  }

  @PostMapping(consumes = "application/json", produces = "application/json")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<T> create(@Valid @RequestBody T eventSubscriptionTO) {
    if (eventSubscriptionTO.getSubscriptionID() != null) {
      return Mono.error(new CreateException("Id not allowed when creating a new " + getType()));
    }
    return getService().create(eventSubscriptionTO);
  }

  @PutMapping("{id}/secret")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> updateSecret(
      @PathVariable UUID id,
      @Valid @RequestBody EventSubscriptionSecretUpdateTO eventSubscriptionSecretUpdateTO) {
    return getService().updateSecret(id, eventSubscriptionSecretUpdateTO);
  }

  @PutMapping("{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<T> update(@PathVariable UUID id, @Valid @RequestBody T eventSubscriptionTO) {
    if (!Objects.equals(id, eventSubscriptionTO.getSubscriptionID())) {
      return Mono.error(new UpdateException("Id in url does not match id in body"));
    }
    return getService().update(eventSubscriptionTO);
  }

  @DeleteMapping("{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteById(@PathVariable UUID id) {
    return getService().deleteById(id);
  }
}
