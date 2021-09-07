package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.SetId;
import org.dcsa.core.events.model.base.AbstractParty;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.util.MappingUtils;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PartyTO extends AbstractParty implements ModelReferencingTO<Party, String>, SetId<String> {

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

    @Data
    public static class IdentifyingCode {
        private String codeListResponsibleAgencyCode;
        private String partyCode;
    }
}
