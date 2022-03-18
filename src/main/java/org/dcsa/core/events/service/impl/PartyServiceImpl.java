package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.Address;
import org.dcsa.core.events.model.Party;
import org.dcsa.core.events.model.PartyIdentifyingCode;
import org.dcsa.core.events.model.enums.DCSAResponsibleAgencyCode;
import org.dcsa.core.events.model.mapper.PartyMapper;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.repository.PartyIdentifyingCodeRepository;
import org.dcsa.core.events.repository.PartyRepository;
import org.dcsa.core.events.service.AddressService;
import org.dcsa.core.events.service.PartyService;
import org.dcsa.core.exception.CreateException;
import org.dcsa.core.exception.GetException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

@RequiredArgsConstructor
@Service
public class PartyServiceImpl implements PartyService {
    private final AddressService addressService;
    private final PartyRepository partyRepository;
    private final PartyIdentifyingCodeRepository partyCodeListResponsibleAgencyRepository;
    private final PartyMapper partyMapper;
    private static final Address EMPTY_ADDRESS = new Address();

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
                pTo -> partyRepository.save(partyMapper.dtoToParty(pTo)))
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
        DCSAResponsibleAgencyCode dcsaCode = idc.getDcsaResponsibleAgencyCode();
        if (dcsaCode == null) {
            if (idc.getCodeListResponsibleAgencyCode() == null) {
                throw new CreateException("Either DCSAResponsibleAgencyCode or codeListResponsibleAgencyCode must be provided");
            }
            try {
                dcsaCode = DCSAResponsibleAgencyCode.legacyCode2DCSACode(idc.getCodeListResponsibleAgencyCode());
            } catch (IllegalArgumentException e) {
                throw new CreateException("Unknown codeListResponsibleAgencyCode!");
            }
        } else if (idc.getCodeListResponsibleAgencyCode() != null) {
            if (!dcsaCode.getLegacyAgencyCode().equals(idc.getCodeListResponsibleAgencyCode())) {
                throw new CreateException("DCSAResponsibleAgencyCode and codeListResponsibleAgencyCode do not match. "
                        + dcsaCode + " (" + dcsaCode.getLegacyAgencyCode() + ") vs. " + idc.getCodeListResponsibleAgencyCode());
            }
        }
        partyCodeListResponsibleAgency.setDcsaResponsibleAgencyCode(dcsaCode);
        partyCodeListResponsibleAgency.setPartyCode(idc.getPartyCode());
        partyCodeListResponsibleAgency.setCodeListName(idc.getCodeListName());
        return partyCodeListResponsibleAgency;
    };

    @Override
    public Mono<PartyTO> findTOById(String partyID) {
        return partyRepository.findById(partyID)
                .switchIfEmpty(Mono.error(new GetException("Cannot find party with ID: " + partyID)))
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
                .dcsaResponsibleAgencyCode(partyIdentifyingCode.getDcsaResponsibleAgencyCode())
                .codeListResponsibleAgencyCode(partyIdentifyingCode.getDcsaResponsibleAgencyCode().getLegacyAgencyCode())
                .codeListName(partyIdentifyingCode.getCodeListName())
                .build();
    }

    private String findNmftaCode(List<PartyTO.IdentifyingCode> identifyingCodes){
        if(null == identifyingCodes || identifyingCodes.isEmpty()){return null;}

        for (PartyTO.IdentifyingCode idc : identifyingCodes) {
            if(DCSAResponsibleAgencyCode.SCAC
                    .getLegacyAgencyCode()
                    .equals(idc.getCodeListResponsibleAgencyCode())){
                return idc.getPartyCode();
            }
        }
        return null;
    }
}
