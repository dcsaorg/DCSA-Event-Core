package org.dcsa.core.events.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public enum SignatureMethod {
    HMAC_SHA256("sha256", "HmacSHA256", 32, 64),
    PLAINTEXT_PASSWORD("plain", "", 1, 1024),
    ;

    private static final Map<String, SignatureMethod> BY_TAG;

    @JsonValue
    private final String signatureMethodTag;
    private final String javaAlgorithmName;
    private final int minKeyLength;
    private final int maxKeyLength;

    SignatureMethod(String signatureMethodTag, String javaAlgorithmName, int minKeyLength, int maxKeyLength) {
        this.signatureMethodTag = signatureMethodTag;
        this.javaAlgorithmName = javaAlgorithmName;
        this.minKeyLength = minKeyLength;
        this.maxKeyLength = maxKeyLength;
    }

    public static Optional<SignatureMethod> byTag(String signatureMethodTag) {
        return Optional.ofNullable(BY_TAG.get(signatureMethodTag));
    }

    public byte[] generateSecret() {
        if (this == SignatureMethod.PLAINTEXT_PASSWORD) {
            throw new UnsupportedOperationException("Not implemented for method: " + this);
        }
        SecureRandom random = new SecureRandom();
        final int length;
        if (this.getMinKeyLength() == this.getMaxKeyLength()) {
            length = this.getMaxKeyLength();
        } else {
            int diff = this.getMaxKeyLength() - this.getMinKeyLength();
            length = this.getMinKeyLength() + random.nextInt(diff);
            assert length >= this.getMinKeyLength();
            assert length <= this.getMaxKeyLength();
        }
        byte[] secret = new byte[length];
        random.nextBytes(secret);
        return secret;
    }

    static {
        SignatureMethod[] methods = SignatureMethod.values();
        Map<String, SignatureMethod> map = new HashMap<>(methods.length);
        for (SignatureMethod method : methods) {
            map.put(method.getSignatureMethodTag(), method);
        }
        BY_TAG = Collections.unmodifiableMap(map);
    }
}
