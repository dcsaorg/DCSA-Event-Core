package org.dcsa.core.events.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CommunicationChannel {
  EI("EDI transmission"),
  EM("Email"),
  AO("API");

  private final String value;
}
