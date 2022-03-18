package org.dcsa.core.events.service.impl;

import org.dcsa.core.events.model.EventSubscription;
import org.dcsa.core.events.model.base.AbstractEventSubscription;
import org.dcsa.core.events.model.enums.*;
import org.dcsa.core.events.model.transferobjects.EventSubscriptionSecretUpdateTO;
import org.dcsa.core.events.repository.EventSubscriptionRepository;
import org.dcsa.core.events.service.EventSubscriptionService;
import org.dcsa.core.events.service.EventSubscriptionTOService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.UpdateException;
import org.dcsa.core.extendedrequest.ExtendedRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class EventSubscriptionTOServiceImpl<
        T extends AbstractEventSubscription,
        S extends EventSubscriptionService,
        R extends EventSubscriptionRepository>
    implements EventSubscriptionTOService<T> {

  public static final List<TransportDocumentTypeCode> ALL_TRANSPORT_DOCUMENT_TYPES =
      List.of(TransportDocumentTypeCode.values());

  public static final List<ShipmentEventTypeCode> ALL_SHIPMENT_EVENT_TYPES =
      List.of(ShipmentEventTypeCode.values());

  public static final List<TransportEventTypeCode> ALL_TRANSPORT_EVENT_TYPES =
      List.of(TransportEventTypeCode.values());

  public static final List<EquipmentEventTypeCode> ALL_EQUIPMENT_EVENT_TYPES =
      List.of(EquipmentEventTypeCode.values());

  public static final List<OperationsEventTypeCode> ALL_OPERATIONS_EVENT_TYPES =
      List.of(OperationsEventTypeCode.values());

  protected abstract S getService();

  protected abstract R getRepository();

  protected abstract Flux<T> mapManyD2TO(Flux<EventSubscription> eventSubscriptionFlux);

  protected abstract Mono<T> mapSingleD2TO(Mono<EventSubscription> eventSubscriptionMono);

  protected abstract Function<T, EventSubscription> eventSubscriptionTOToEventSubscription();

  protected abstract Function<EventSubscription, T> eventSubscriptionToEventSubscriptionTo();

  protected abstract List<EventType> getEventTypesForTo(T eventSubscriptionTO);

  protected abstract List<TransportDocumentTypeCode> getTransportDocumentTypesForTo(
      T eventSubscriptionTO);

  protected abstract List<ShipmentEventTypeCode> getShipmentEventTypeCodesForTo(
      T eventSubscriptionTO);

  protected abstract List<TransportEventTypeCode> getTransportEventTypeCodesForTo(
      T eventSubscriptionTO);

  protected abstract List<EquipmentEventTypeCode> getEquipmentEventTypeCodesForTo(
      T eventSubscriptionTO);

  protected abstract List<OperationsEventTypeCode> getOperationsEventTypeCodesForTo(
      T eventSubscriptionTO);

  protected Mono<Object> validateUpdateRequest(T eventSubscription) {
    if (eventSubscription.getSecret() != null) {
      return Mono.error(
          new UpdateException(
              "Please omit the \"secret\" attribute.  If you want to change the"
                  + " secret, please use the dedicated secret endpoint"
                  + " (\"PUT .../event-subscriptions/"
                  + eventSubscription.getSubscriptionID()
                  + "/secret\")."));
    }
    return Mono.just(eventSubscription);
  }

  protected Mono<Object> validateCreateRequest(T eventSubscription) {
    if (eventSubscription.getSecret() == null) {
      return Mono.error(
          new CreateException("\"secret\" attribute is required to create an event subscription."));
    }
    return Mono.just(eventSubscription);
  }

  protected Mono<T> createEventTypes(T eventSubscriptionTO) {
    return Flux.fromIterable(getEventTypesForTo(eventSubscriptionTO))
        .concatMap(
            eventType ->
                getRepository()
                    .insertEventTypeForSubscription(
                        eventSubscriptionTO.getSubscriptionID(), eventType))
        .then(Mono.just(eventSubscriptionTO));
  }

  protected Mono<Void> createTransportDocumentEventTypes(T eventSubscriptionTO) {

    return Flux.fromIterable(getTransportDocumentTypesForTo(eventSubscriptionTO))
        .flatMap(
            td ->
                getRepository()
                    .insertTransportDocumentEventTypeForSubscription(
                        eventSubscriptionTO.getSubscriptionID(), td))
        .then();
  }

  protected Mono<Void> createShipmentEventType(T eventSubscriptionTO) {

    return Flux.fromIterable(getShipmentEventTypeCodesForTo(eventSubscriptionTO))
        .flatMap(
            s ->
                getRepository()
                    .insertShipmentEventTypeForSubscription(
                        eventSubscriptionTO.getSubscriptionID(), s))
        .then();
  }

  protected Mono<Void> createTransportEventType(T eventSubscriptionTO) {

    return Flux.fromIterable(getTransportEventTypeCodesForTo(eventSubscriptionTO))
        .flatMap(
            t ->
                getRepository()
                    .insertTransportEventTypeForSubscription(
                        eventSubscriptionTO.getSubscriptionID(), t))
        .then();
  }

  protected Mono<Void> createEquipmentEventType(T eventSubscriptionTO) {

    return Flux.fromIterable(getEquipmentEventTypeCodesForTo(eventSubscriptionTO))
        .flatMap(
            e ->
                getRepository()
                    .insertEquipmentEventTypeForSubscription(
                        eventSubscriptionTO.getSubscriptionID(), e))
        .then();
  }

  protected Mono<Void> createOperationsEventType(T eventSubscriptionTO) {

    return Flux.fromIterable(getOperationsEventTypeCodesForTo(eventSubscriptionTO))
        .flatMap(
            o ->
                getRepository()
                    .insertOperationsEventTypeForSubscription(
                        eventSubscriptionTO.getSubscriptionID(), o))
        .then();
  }

  @Override
  public Mono<T> findById(UUID id) {
    return mapSingleD2TO(findShallowEventSubscriptionById(id));
  }

  protected abstract Mono<EventSubscription> findShallowEventSubscriptionById(UUID id);

  @Override
  public Flux<T> findAllExtended(ExtendedRequest<EventSubscription> extendedRequest) {
    return mapManyD2TO(getService().findAllExtended(extendedRequest));
  }

  @Override
  public Mono<Void> updateSecret(
      UUID subscriptionID, EventSubscriptionSecretUpdateTO eventSubscriptionSecretUpdateTO) {
    return getService()
        .findById(subscriptionID)
        .doOnNext(
            eventSubscription ->
                eventSubscription.setSecret(eventSubscriptionSecretUpdateTO.getSecret()))
        .flatMap(getService()::update)
        .then();
  }

  @Override
  public Mono<Void> deleteById(UUID id) {
    return getService().deleteById(id);
  }

  protected final Function<String, List<EventType>> stringToEventTypeList =
      s -> {
        if (s.contains(",")) {
          return Arrays.stream(s.split(",")).map(EventType::valueOf).collect(Collectors.toList());
        } else {
          return Stream.of(s).map(EventType::valueOf).collect(Collectors.toList());
        }
      };
}
