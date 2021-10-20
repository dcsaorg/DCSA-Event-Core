package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.PartyIdentifyingCode;
import org.dcsa.core.events.model.enums.CodeListResponsibleAgency;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.repository.PartyIdentifyingCodeRepository;
import org.dcsa.core.events.repository.PartyRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
public class PartyServiceImpl extends ExtendedBaseServiceImpl<PartyRepository, Party, String> implements PartyService {
    private final AddressService addressService;
    private final PartyRepository partyRepository;
    private final PartyIdentifyingCodeRepository partyCodeListResponsibleAgencyRepository;
    private static final Address EMPTY_ADDRESS = new Address();

    @Override
    public PartyRepository getRepository() {
        return partyRepository;
    }

    public Flux<Party> findAllById(Iterable<String> ids) {
        return partyRepository.findAllById(ids);
    }

    @Override
    public Mono<PartyTO> ensureResolvable(PartyTO partyTO) {
        Address address = partyTO.getAddress();
        Mono<PartyTO> partyTOMono;
        if (address != null) {
            partyTOMono = addressService.ensureResolvable(address)
                    .doOnNext(partyTO::setAddress)
                    .thenReturn(partyTO);
        } else {
            partyTOMono = Mono.just(partyTO);
        }

        return partyTOMono
            .flatMap(
                pTo -> this.create(pTo.toParty()))
            .flatMap(
                party ->
                        Mono.justOrEmpty(partyTO.getIdentifyingCodes())
                            .flatMapMany(Flux::fromIterable)
                            .map(idc -> mapIdcCodeToPartyIdc.apply(party.getId(), idc))
                            .flatMap(partyCodeListResponsibleAgencyRepository::save)
                            .then()
                            .thenReturn(party))
            .map(
                party ->
                    party.toPartyTO(
                        partyTO.getNmftaCode(), partyTO.getAddress(), partyTO.getIdentifyingCodes()));
    }

    private final BiFunction<String, PartyTO.IdentifyingCode, PartyIdentifyingCode> mapIdcCodeToPartyIdc = (partyId, idc) -> {
        PartyIdentifyingCode partyCodeListResponsibleAgency = new PartyIdentifyingCode();
        partyCodeListResponsibleAgency.setPartyID(partyId);
        partyCodeListResponsibleAgency.setCodeListResponsibleAgencyCode(idc.getCodeListResponsibleAgencyCode());
        partyCodeListResponsibleAgency.setPartyCode(idc.getPartyCode());
        partyCodeListResponsibleAgency.setCodeListName(idc.getCodeListName());
        return partyCodeListResponsibleAgency;
    };

    @Override
    public Mono<PartyTO> findTOById(String partyID) {
        return findById(partyID)
                .flatMap(this::loadRelatedEntities);
    }
    private  Mono<PartyTO> loadRelatedEntities(Party party){

        Mono<Address> addressMono = Mono.justOrEmpty(party.getAddressID())
                                    .flatMap(addressService::findById)
                                    .switchIfEmpty(Mono.just(EMPTY_ADDRESS));

        Mono<List<PartyTO.IdentifyingCode>> sartIdentifyingCodes = partyCodeListResponsibleAgencyRepository.findAllByPartyID(party.getId())
                .map(
                        this::partyIdentifyingCodeToIdentifyingCode
                )
                .collectList()
                .switchIfEmpty(Mono.just(Collections.emptyList()));

        return Mono.zip(addressMono,sartIdentifyingCodes)
                .map(tuple -> party.toPartyTO(
                        findNmftaCode(tuple.getT2()),
                        tuple.getT1().equals(EMPTY_ADDRESS) ? null : tuple.getT1(),
                        tuple.getT2()
                ));
    }

    private PartyTO.IdentifyingCode partyIdentifyingCodeToIdentifyingCode(PartyIdentifyingCode partyIdentifyingCode){
        return PartyTO.IdentifyingCode.builder()
                .partyCode(partyIdentifyingCode.getPartyCode())
                .codeListResponsibleAgencyCode(partyIdentifyingCode.getCodeListResponsibleAgencyCode())
                .codeListName(partyIdentifyingCode.getCodeListName())
                .build();
    }

    private String findNmftaCode(List<PartyTO.IdentifyingCode> identifyingCodes){
        if(null == identifyingCodes || identifyingCodes.isEmpty()){return null;}

        for (PartyTO.IdentifyingCode idc : identifyingCodes) {
            if(CodeListResponsibleAgency.SCAC
                    .getCode()
                    .equals(idc.getCodeListResponsibleAgencyCode())){
                return idc.getPartyCode();
            }
        }
        return null;
    }
}
