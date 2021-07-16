package org.dcsa.core.events.repository;

import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.repository.ExtendedRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PartyRepository extends ExtendedRepository<Party, String> {
    Mono<Party> findByAddressIDAndPartyNameAndTaxReference1AndTaxReference2AndPublicKeyAndNmftaCode(
            UUID addressID,
            String partyName,
            String taxReference1,
            String taxReference2,
            String publicKey,
            String nmftaCode
    );

    default Mono<Party> findByContent(PartyTO partyTO) {
        Address address = partyTO.getAddress();
        UUID addressID = address != null ? address.getId() : null;
        return findByAddressIDAndPartyNameAndTaxReference1AndTaxReference2AndPublicKeyAndNmftaCode(
                addressID,
                partyTO.getPartyName(),
                partyTO.getTaxReference1(),
                partyTO.getTaxReference2(),
                partyTO.getPublicKey(),
                partyTO.getNmftaCode()
        );
    }

}
