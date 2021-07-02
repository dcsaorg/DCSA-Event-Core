package org.dcsa.core.events.service.impl;

import lombok.RequiredArgsConstructor;
import org.dcsa.core.events.model.TransportCall;
import org.dcsa.core.events.repository.TransportCallRepository;
import org.dcsa.core.events.service.TransportCallService;
import org.dcsa.core.service.impl.ExtendedBaseServiceImpl;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class TransportCallServiceImpl extends ExtendedBaseServiceImpl<TransportCallRepository, TransportCall, String> implements TransportCallService {
    private final TransportCallRepository transportCallRepository;

    @Override
    public TransportCallRepository getRepository() {
        return transportCallRepository;
    }

}
