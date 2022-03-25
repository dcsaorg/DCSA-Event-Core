package org.dcsa.core.events.edocumentation.service.impl;

import lombok.AllArgsConstructor;
import org.dcsa.core.events.edocumentation.model.mapper.ConsignmentItemMapper;
import org.dcsa.core.events.edocumentation.service.ConsignmentItemService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ConsignmentItemServiceImpl implements ConsignmentItemService {
  private final ConsignmentItemMapper consignmentItemMapper;
  // TODO
}
