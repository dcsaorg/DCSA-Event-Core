package org.dcsa.core.events.model;

import org.dcsa.core.events.model.transferobjects.TransportCallTO;

public interface TransportCallBasedEvent {

    String getTransportCallID();
    void setTransportCallID(String transportCallID);

    TransportCallTO getTransportCall();
    void setTransportCall(TransportCallTO transportCall);
}
