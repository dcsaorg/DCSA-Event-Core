package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.repository.TransportCallTORepository;
import org.dcsa.core.events.service.TransportCallTOService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransportCallTOServiceImpl extends ExtendedBaseServiceImpl<TransportCallTORepository, TransportCallTO, String> implements TransportCallTOService {
    private final TransportCallTORepository transportCallTORepository;

    @Override
    public TransportCallTORepository getRepository() {
        return transportCallTORepository;
    }

}
