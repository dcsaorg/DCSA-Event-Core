package org.dcsa.core.events.controller;

import org.dcsa.core.controller.BaseController;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.service.EventService;
import org.dcsa.core.exception.GetException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@RequestMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
public abstract class AbstractEventController<S extends EventService<T>, T extends Event> extends BaseController<S, T, UUID> {

    @Autowired
    protected ExtendedParameters extendedParameters;

    @Autowired
    protected R2dbcDialect r2dbcDialect;

    @Override
    public String getType() {
        return getService().getModelClass().getSimpleName();
    }

    protected abstract ExtendedRequest<T> newExtendedRequest();

    @GetMapping
    public Flux<T> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        ExtendedRequest<T> extendedRequest = newExtendedRequest();
        try {
            extendedRequest.parseParameter(request.getQueryParams());
        } catch (GetException getException) {
            return Flux.error(getException);
        }

        return getService().findAllExtended(extendedRequest)
                .doOnComplete(
                        () -> extendedRequest.insertHeaders(response, request)
                );
    }

    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<T> findById(@PathVariable UUID id) {
        return super.findById(id);
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<T> create(@Valid @RequestBody T event) {
        return super.create(event);
    }

    @PutMapping({"{id}"})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<T> update(@PathVariable UUID id, @RequestBody T event) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> delete(@RequestBody T event) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping({"{id}"})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Mono<Void> deleteById(@PathVariable UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

}
