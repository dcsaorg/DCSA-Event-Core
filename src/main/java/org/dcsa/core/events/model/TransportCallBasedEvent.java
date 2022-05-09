package org.dcsa.core.events.model;

import org.dcsa.core.events.model.transferobjects.TransportCallTO;

import java.util.UUID;

public interface TransportCallBasedEvent {

    UUID getTransportCallID();
    void setTransportCallID(UUID transportCallID);

    TransportCallTO getTransportCall();
    void setTransportCall(TransportCallTO transportCall);
}
