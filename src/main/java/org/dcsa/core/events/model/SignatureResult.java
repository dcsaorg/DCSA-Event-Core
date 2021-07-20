package org.dcsa.core.events.model;

import lombok.Data;
import org.dcsa.core.events.model.enums.SignatureMethod;

@Data(staticConstructor = "of")
public class SignatureResult<T> {
    private final String result;
    private final boolean valid;
    private final SignatureMethod signatureMethod;
    private final T parsed;

    public static <T> SignatureResult<T> of(String result) {
        return of(result, null);
    }

    public static <T> SignatureResult<T> of(String result, SignatureMethod signatureMethod) {
        return of(result, false, signatureMethod, null);
    }
}
