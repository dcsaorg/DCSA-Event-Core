package org.dcsa.core.events.model.transferobjects;

import lombok.Data;
import org.dcsa.core.events.model.enums.PartyFunction;
import org.dcsa.core.exception.ConcreteRequestErrorMessageException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
public class DocumentPartyTO {

	@Valid
	@NotNull(message = "Party is required.")
	private PartyTO party;

	@NotNull(message = "PartyFunction is required.")
	private PartyFunction partyFunction;

	private List<String> displayedAddress;

	@NotNull(message = "IsToBeNotified is required.")
	private Boolean isToBeNotified;

	public void setDisplayedAddress(List<String> displayedAddress) {
    Optional.ofNullable(displayedAddress)
        .ifPresentOrElse(
            strings -> {
              for (String da : displayedAddress) {
                if (da.length() > 250) {
                  throw ConcreteRequestErrorMessageException.invalidParameter(
                      "A single displayedAddress has a max size of 250.");
                }
              }
              this.displayedAddress = strings;
            },
            () -> this.displayedAddress = Collections.emptyList());
	}
}
