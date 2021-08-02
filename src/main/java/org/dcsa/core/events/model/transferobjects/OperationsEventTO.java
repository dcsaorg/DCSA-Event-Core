package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractOperationsEvent;
import org.springframework.data.annotation.Transient;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationsEventTO extends AbstractOperationsEvent {

    private PartyTO publisher;

    @Transient
    private TransportCallTO transportCall;

    @Transient
    private LocationTO vesselPosition;
}
