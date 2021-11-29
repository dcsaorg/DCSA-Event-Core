package org.dcsa.core.events.model.transferobjects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartyContactDetailsTO {
  private String name;
  private String phone;
  private String email;
}
