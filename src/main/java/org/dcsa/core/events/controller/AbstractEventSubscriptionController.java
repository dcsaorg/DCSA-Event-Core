package org.dcsa.core.events.controller;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.controller.BaseController;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.extendedrequest.ExtendedParameters;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import org.dcsa.core.service.BaseService;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@RequestMapping(
    value = "event-subscriptions",
    produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public abstract class AbstractEventSubscriptionController<
        S extends BaseService<T, UUID>, T extends AbstractEventSubscription>
    extends BaseController<S, T, UUID> {

  protected final ExtendedParameters extendedParameters;

  protected final R2dbcDialect r2dbcDialect;

  protected ExtendedRequest<T> newExtendedRequest(Class<T> clazz) {
    return new ExtendedRequest<>(extendedParameters, r2dbcDialect, clazz);
  }
}
