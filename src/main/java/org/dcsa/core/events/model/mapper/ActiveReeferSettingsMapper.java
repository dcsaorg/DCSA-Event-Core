package org.dcsa.core.events.model.mapper;

import org.dcsa.core.events.model.ActiveReeferSettings;
import org.dcsa.core.events.model.transferobjects.ActiveReeferSettingsTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ActiveReeferSettingsMapper {

	ActiveReeferSettingsTO activeReeferSettingsToDTO(ActiveReeferSettings activeReeferSettings);

	@Mapping(source = "shipmentEquipmentID", target = "shipmentEquipmentID")
	ActiveReeferSettings dtoToActiveReeferSettings(ActiveReeferSettingsTO activeReeferSettingsTO, UUID shipmentEquipmentID);
}
