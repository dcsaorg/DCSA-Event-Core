package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.SetId;
import org.dcsa.core.events.model.VesselPosition;
import org.dcsa.core.events.model.base.AbstractVesselPosition;
import org.dcsa.core.events.util.Util;
import org.dcsa.core.util.MappingUtils;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class VesselPositionTO extends AbstractVesselPosition implements ModelReferencingTO<VesselPosition, String>, SetId<String> {

    @Override
    public boolean isSolelyReferenceToModel() {
        return Util.containsOnlyID(this, VesselPositionTO::new);
    }

    public boolean isEqualsToModel(VesselPosition other) {
        return this.toVesselPosition().equals(other);
    }

    public VesselPosition toVesselPosition() {
        return MappingUtils.instanceFrom(this, VesselPosition::new, AbstractVesselPosition.class);
    }
}
