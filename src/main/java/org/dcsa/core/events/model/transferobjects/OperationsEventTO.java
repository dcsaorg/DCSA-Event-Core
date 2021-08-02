package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractOperationsEvent;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationsEventTO extends AbstractOperationsEvent {

    private PartyTO publisher;
}
