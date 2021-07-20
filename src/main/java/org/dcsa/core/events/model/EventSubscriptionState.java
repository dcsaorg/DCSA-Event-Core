package org.dcsa.core.events.model;

import org.dcsa.core.events.model.enums.SignatureMethod;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface EventSubscriptionState {

    UUID getSubscriptionID();
    String getCallbackUrl();
    byte[] getSigningKey();
    SignatureMethod getSignatureMethod();

    Long getRetryCount();
    void setRetryCount(Long retryCount);

    Long getAccumulatedRetryDelay();
    void setAccumulatedRetryDelay(Long accumulatedRetryDelay);

    OffsetDateTime getRetryAfter();
    void setRetryAfter(OffsetDateTime retryAfter);

    Integer getLastBundleSize();
    void setLastBundleSize(Integer lastBundleSize);

    default void resetFailureState() {
        setRetryAfter(null);
        setAccumulatedRetryDelay(null);
        setRetryCount(0L);
    }
}
