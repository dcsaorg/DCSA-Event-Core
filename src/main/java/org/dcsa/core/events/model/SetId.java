package org.dcsa.core.events.model;

import org.dcsa.core.model.GetId;

public interface SetId<I> extends GetId<I> {
    void setId(I id);
}
