package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.base.AbstractVesselPosition;
import org.dcsa.core.events.model.transferobjects.PartyTO;
import org.dcsa.core.events.model.transferobjects.VesselPositionTO;
import org.dcsa.core.util.MappingUtils;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Objects;
import java.util.UUID;

@Table("vesselPosition")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VesselPosition extends AbstractVesselPosition implements SetId<String> {

    public VesselPositionTO toVesselPositionTO() {
        return MappingUtils.instanceFrom(this, VesselPositionTO::new, AbstractVesselPosition.class);
    }
}
