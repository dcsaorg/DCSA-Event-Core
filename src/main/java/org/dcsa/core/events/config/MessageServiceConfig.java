package org.dcsa.core.events.config;

import lombok.Getter;
import org.dcsa.core.events.model.enums.SignatureMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Min;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
@Configuration
@ConfigurationProperties(prefix = "dcsa.message-service")
public class MessageServiceConfig {

    @Min(1)
    private int defaultBundleSize = 100;

    @DurationUnit(ChronoUnit.MINUTES)
    private Duration minRetryAfterDelay = Duration.of(60, ChronoUnit.SECONDS);

    @DurationUnit(ChronoUnit.MINUTES)
    private Duration maxRetryAfterDelay = Duration.of(24, ChronoUnit.HOURS);

    private SignatureMethod defaultSignatureMethod;

    @Value("${dcsa.specification.version:N/A}")
    private String apiSpecificationVersion;

    @Autowired
    void initializeDefaultSignatureMethod(@Value("${dcsa.MessageService.defaultSignatureMethod:sha256}") String defaultSignatureMethod) {
        Optional<SignatureMethod> signatureMethodOptional = SignatureMethod.byTag(defaultSignatureMethod);
        if (signatureMethodOptional.isEmpty()) {
            String methods = Arrays.stream(SignatureMethod.values())
                    .map(SignatureMethod::getSignatureMethodTag)
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new IllegalArgumentException("Unknown signature listed in dcsa.MessageService.defaultSignatureMethod: \""
                    + defaultSignatureMethod + "\". Please choose one of: " + methods );
        }
        this.defaultSignatureMethod = signatureMethodOptional.get();
    }
}
