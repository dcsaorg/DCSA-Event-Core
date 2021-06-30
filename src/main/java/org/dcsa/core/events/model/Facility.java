package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dcsa.core.events.model.base.AbstractFacility;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(callSuper = true)
public class Facility extends AbstractFacility {

}
