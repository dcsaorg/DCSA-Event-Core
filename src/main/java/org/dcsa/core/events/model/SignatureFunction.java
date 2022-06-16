package org.dcsa.core.events.model;

import org.apache.commons.codec.binary.Hex;
import org.dcsa.core.events.model.enums.SignatureMethod;

import java.security.MessageDigest;

public interface SignatureFunction {
    SignatureMethod getSignatureMethod();

    default String getJavaAlgorithmName() {
        return getSignatureMethod().getJavaAlgorithmName();
    }

    byte[] computeSignature(byte[] key, byte[] payload) throws Exception;

    default int getMinKeyLength() {
        return getSignatureMethod().getMinKeyLength();
    }
    default int getMaxKeyLength() {
        return getSignatureMethod().getMaxKeyLength();
    }

    default byte[] parseSignature(String value) throws Exception {
        return Hex.decodeHex(value);
    }

    default String computeSignatureString(byte[] key, byte[] payload) throws Exception {
        byte[] signature = this.computeSignature(key, payload);
        return Hex.encodeHexString(signature);
    }

    default boolean verifySignature(byte[] key, byte[] payload, byte[] providedSignature) throws Exception {
        byte[] computedSignature = computeSignature(key, payload);
        return MessageDigest.isEqual(computedSignature, providedSignature);
    }

    default String headerSignatureHeaderValue(byte[] key, byte[] payload) throws Exception {
        return getSignatureMethod().getSignatureMethodTag() + "=" + this.computeSignatureString(key, payload);
    }
}
