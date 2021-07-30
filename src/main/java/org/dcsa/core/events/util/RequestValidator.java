package org.dcsa.core.events.util;

import java.util.Map;

@FunctionalInterface
public interface RequestValidator {
  void validate(Map<String, String> queryParams);
}
