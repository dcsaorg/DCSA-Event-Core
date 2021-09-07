package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.SetId;
import org.dcsa.core.events.model.base.AbstractParty;
import org.dcsa.core.events.model.enums.CodeListResponsibleAgency;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.util.MappingUtils;

import org.dcsa.core.exception.InvalidParameterException;
import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@JsonPropertyOrder({ "nmftaCode", "identifyingCodes" })
public class PartyTO extends AbstractParty implements ModelReferencingTO<Party, String>, SetId<String> {

    private String nmftaCode;

    private Address address;

    private List<IdentifyingCode> identifyingCodes;

    @Override
    public boolean isSolelyReferenceToModel() {
        return Util.containsOnlyID(this, PartyTO::new);
    }

    public boolean isEqualsToModel(Party other) {
        return this.toParty().equals(other);
    }

    public Party toParty() {
        Party party = MappingUtils.instanceFrom(this, Party::new, AbstractParty.class);
        if (this.address != null) {
            party.setAddressID(address.getId());
        }
        return party;
    }

      public void adjustIdentifyingCodesIfNmftaIsPresent(){
          if (StringUtils.isNotEmpty(this.getNmftaCode())) {
              if (null != identifyingCodes
                      && !identifyingCodes.isEmpty()
                      && identifyingCodes.stream()
                      .anyMatch(
                              idc ->
                                      CodeListResponsibleAgency.SCAC
                                              .getCode()
                                              .equals(idc.getCodeListResponsibleAgencyCode()))) {
                  throw new InvalidParameterException(
                          "nmfta code is present along with SCAC in identifyingCodes");
              } else if (null == identifyingCodes || identifyingCodes.isEmpty()) {
                  this.identifyingCodes =
                          Collections.singletonList(
                                  new IdentifyingCode(CodeListResponsibleAgency.SCAC.getCode(), this.getNmftaCode()));
              } else {
                  identifyingCodes.add(
                          new IdentifyingCode(CodeListResponsibleAgency.SCAC.getCode(), this.getNmftaCode()));
              }
          }
      }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdentifyingCode {
        private String codeListResponsibleAgencyCode;
        private String partyCode;
    }
}
