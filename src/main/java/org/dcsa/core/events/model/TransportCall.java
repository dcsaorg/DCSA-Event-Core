package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.base.AbstractTransportCall;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class TransportCall extends AbstractTransportCall implements Persistable<String> {

    @Transient
    @JsonIgnore
    private boolean isNewRecord;

    @JsonIgnore
    @Override
    public String getId() {
        return this.transportCallID;
    }

    @JsonIgnore
    @Override
    public boolean isNew() {
        return this.isNewRecord || this.getId() == null;
    }
}
