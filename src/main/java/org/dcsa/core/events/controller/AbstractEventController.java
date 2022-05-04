package org.dcsa.core.events.controller;

import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.service.EventService;
import org.dcsa.core.exception.DCSAException;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequestMapping(
        value = "events",
        method = {RequestMethod.GET},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public abstract class AbstractEventController<S extends EventService<T>, T extends Event>  {

    @Autowired
    protected ExtendedParameters extendedParameters;

    @Autowired
    protected R2dbcDialect r2dbcDialect;

    public abstract S getService();

    protected abstract ExtendedRequest<T> newExtendedRequest();

    public Flux<T> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        ExtendedRequest<T> extendedRequest = newExtendedRequest();
        try {
            extendedRequest.parseParameter(request.getQueryParams());
        } catch (DCSAException ex) {
            return Flux.error(ex);
        }

        return getService().findAllExtended(extendedRequest)
                .doOnComplete(
                        () -> extendedRequest.insertHeaders(response, request)
                );
    }

    @GetMapping(value="{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<T> findById(@PathVariable UUID id) {
        return getService().findById(id);
    }
}
