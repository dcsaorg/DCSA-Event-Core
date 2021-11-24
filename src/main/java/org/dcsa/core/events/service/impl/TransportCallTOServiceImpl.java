package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.transferobjects.TransportCallTO;
import org.dcsa.core.events.repository.TransportCallTORepository;
import org.dcsa.core.events.service.TransportCallTOService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransportCallTOServiceImpl extends AbstractTransportCallTOServiceImpl<TransportCallTORepository, TransportCallTO> implements TransportCallTOService {

    private final TransportCallTORepository transportCallTORepository;

    @Override
    public TransportCallTORepository getRepository() {
        return transportCallTORepository;
    }
}
