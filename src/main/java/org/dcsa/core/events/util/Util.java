package org.dcsa.core.events.util;

import org.dcsa.core.events.model.SetId;
import org.dcsa.core.events.model.transferobjects.ModelReferencingTO;
import org.dcsa.core.exception.UpdateException;
import org.dcsa.core.model.GetId;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A class to handle the fact that an Event can have multiple subEvents (Transport, Equipment, Shipment). Used primarily
 * when converting between JSON property names, POJO field names and DB column names. Also handles BillOfLading filtering
 * as this requires a Join with the Shipment table
 */
public class Util {

    public static <TO, M, ID> Mono<M> createOrFindByContent(TO instanceTO, Function<TO, Mono<M>> findByContent, Function<TO, Mono<M>> create) {
        return findByContent.apply(instanceTO)
                .switchIfEmpty(Mono.defer(() -> create.apply(instanceTO)))
                .doOnNext(m -> {
                    if (instanceTO instanceof SetId && m instanceof GetId) {
                        @SuppressWarnings({"unchecked"})
                        SetId<ID> s = (SetId<ID>) instanceTO;
                        @SuppressWarnings({"unchecked"})
                        GetId<ID> g = (GetId<ID>) m;
                        s.setId(g.getId());
                    }
                });
    }

    public static <T extends SetId<I>, I> boolean containsOnlyID(T model, Supplier<T> constructor) {
        I id = model.getId();
        if (id != null) {
            T t = constructor.get();
            if (t.getClass() != model.getClass()) {
                throw new IllegalArgumentException("Logic error: this method assumes that the class is the same");
            }
            t.setId(id);
            return model.equals(t);
        }
        return false;
    }

    public static <TO extends ModelReferencingTO<M, ID>, M extends GetId<ID>, ID> Mono<M> resolveModelReference(TO instanceTO, Function<ID, Mono<M>> findByID, Function<TO, Mono<M>> create, String entityName) {
        ID id = Objects.requireNonNull(instanceTO).getId();
        if (id != null) {
            return findByID.apply(id)
                    .doOnNext(m -> {
                        if (!instanceTO.isSolelyReferenceToModel() && !instanceTO.isEqualsToModel(m)) {
                            throw new UpdateException(entityName + " with id " + id
                                    + " exists but has a different content. Remove the ID field to"
                                    + " create a new instance or provide an update");
                        }
                    });
        } else {
            return create.apply(instanceTO)
                    .doOnNext(m -> {
                        if (m instanceof SetId) {
                            @SuppressWarnings({"rawtypes", "unchecked"})
                            SetId<ID> s = ((SetId) instanceTO);
                            s.setId(m.getId());
                        }
                    });
        }
    }

}
