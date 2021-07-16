package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.enums.DocumentReferenceType;

@Data
@RequiredArgsConstructor(staticName = "of")
public class DocumentReferenceTO {
    private final DocumentReferenceType documentReferenceType;
    private final String documentReferenceValue;
}
