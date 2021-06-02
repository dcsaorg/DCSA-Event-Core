package org.dcsa.core.events.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.events.model.Event;
import org.dcsa.core.events.service.EventService;
import org.dcsa.core.events.util.ExtendedGenericEventRequest;
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
@Tag(name = "Events", description = "The event API")
public abstract class AbstractEventController<S extends EventService<Event>> extends BaseController<S, Event, UUID> {

    @Autowired
    protected ExtendedParameters extendedParameters;

    @Autowired
    protected R2dbcDialect r2dbcDialect;

    @Override
    public String getType() {
        return getService().getModelClass().getSimpleName();
    }

    protected ExtendedRequest<? extends Event> newExtendedRequest() {
        return new ExtendedGenericEventRequest(extendedParameters, r2dbcDialect);
    }

    @Operation(summary = "Find all Events", description = "Finds all Events in the database", tags = { "Events" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Event.class))))
    })
    @GetMapping
    public Flux<Event> findAll(ServerHttpResponse response, ServerHttpRequest request) {
        @SuppressWarnings({"unchecked"})
        ExtendedRequest<Event> extendedRequest = (ExtendedRequest<Event>)newExtendedRequest();
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

    @Operation(summary = "Find Event by ID", description = "Returns a single Event", tags = { "Event" }, parameters = {
            @Parameter(in = ParameterIn.PATH, name = "id", description="Id of the Event to be obtained. Cannot be empty.", required=true),
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Event not found")
    })
    @GetMapping(value="{id}", produces = "application/json")
    @Override
    public Mono<Event> findById(@PathVariable UUID id) {
        return super.findById(id);
    }

    @Operation(summary = "Save any type of event", description = "Saves any type of event", tags = { "Events" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    @PostMapping(consumes = "application/json", produces = "application/json")
    @Override
    public Mono<Event> create(@Valid @RequestBody Event event) {
        return super.create(event);
    }

    @PutMapping({"{id}"})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ApiResponse(responseCode = "403", description = "Changes to events are not permitted")
    public Mono<Event> update(@PathVariable UUID id, @RequestBody Event event) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ApiResponse(responseCode = "403", description = "Deletion of events are not permitted")
    public Mono<Void> delete(@RequestBody Event event) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

    @DeleteMapping({"{id}"})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ApiResponse(responseCode = "403", description = "Deletion of events are not permitted")
    public Mono<Void> deleteById(@PathVariable UUID id) {
        return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
    }

}
