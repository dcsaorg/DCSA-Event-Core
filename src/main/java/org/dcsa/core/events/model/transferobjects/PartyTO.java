package org.dcsa.core.events.model.transferobjects;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.SetId;
import org.dcsa.core.events.model.base.AbstractParty;
import org.dcsa.core.events.model.enums.DCSAResponsibleAgencyCode;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.util.MappingUtils;

import java.util.Collections;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
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
                                      DCSAResponsibleAgencyCode.SCAC
                                              .getLegacyAgencyCode()
                                              .equals(idc.getCodeListResponsibleAgencyCode()))) {

                  for (IdentifyingCode idc : this.identifyingCodes) {
                      if(DCSAResponsibleAgencyCode.SCAC
                              .getLegacyAgencyCode()
                              .equals(idc.getCodeListResponsibleAgencyCode())){
                            idc.setPartyCode(this.getNmftaCode());
                      }
                  }

              } else if (null == identifyingCodes || identifyingCodes.isEmpty()) {
                this.identifyingCodes =
                    Collections.singletonList(
                        IdentifyingCode.builder()
                            .codeListResponsibleAgencyCode(DCSAResponsibleAgencyCode.SCAC.getLegacyAgencyCode())
                            .partyCode(this.getNmftaCode())
                            .build());
              } else {
                identifyingCodes.add(
                    IdentifyingCode.builder()
                        .codeListResponsibleAgencyCode(DCSAResponsibleAgencyCode.SCAC.getLegacyAgencyCode())
                        .partyCode(this.getNmftaCode())
                        .build());
              }
          }
      }

    @Data
    @Builder
    public static class IdentifyingCode {
        private DCSAResponsibleAgencyCode DCSAResponsibleAgencyCode;
        private String codeListResponsibleAgencyCode;
        private String partyCode;
        private String codeListName;
    }
}
