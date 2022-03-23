package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentTO;
import org.dcsa.core.events.edocumentation.model.transferobject.ValueAddedServiceRequestTO;
import org.dcsa.core.events.model.base.AbstractShippingInstruction;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShippingInstructionTO extends AbstractShippingInstruction {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String carrierBookingReference;

  private LocationTO placeOfIssue;

  @JsonProperty("utilizedTransportEquipments")
  @Valid
  @NotEmpty
  private List<ShipmentEquipmentTO> shipmentEquipments;

  @Valid private List<DocumentPartyTO> documentParties;

  @Valid private List<ValueAddedServiceRequestTO> valueAddedServiceRequests;

  @Valid private List<ReferenceTO> references;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<ShipmentTO> shipments;

  /**
   * Pull the carrierBookingReference from shipmentEquipments into the ShippingInstruction if
   * possible
   *
   * <p>If the ShipmentEquipments all have the same carrierBookingReference, the value is moved up
   * to this ShippingInstruction and cleared from the ShipmentEquipments.
   *
   * <p>This is useful on output to "prettify" the resulting ShippingInstruction to avoid
   * unnecessary "per ShipmentEquipment" carrier booking references. The method is idempotent.
   *
   * <p>This is more or less the logical opposite of {@link
   * #pushCarrierBookingReferenceIntoShipmentEquipmentIfNecessary()}.
   *
   * @throws IllegalStateException If the ShippingInstruction already has a carrierBookingReference
   *     and it is not exactly the same as it would get after this call.
   * @throws NullPointerException If one or more ShipmentEquipmentTO had a null
   *     carrierBookingReference AND one or more of them had a non-null carrierBookingReference.
   *     (I.e. either they all must have a carrierBookingReference or none of them can have one).
   */
  @JsonIgnore
  public void hoistCarrierBookingReferenceIfPossible() {

    String actualCentralBookingReference = this.getCarrierBookingReference();
    String possibleCentralBookingReference =
        shipmentEquipments.isEmpty()
            ? null
            : shipmentEquipments.get(0).getCarrierBookingReference();
    Boolean allNull = null;
    for (ShipmentEquipmentTO shipmentEquipmentTO : shipmentEquipments) {
      String cargoBookingReference = shipmentEquipmentTO.getCarrierBookingReference();
      if (cargoBookingReference == null) {
        if (allNull == Boolean.FALSE) {
          throw new NullPointerException(
              "One of the CargoItemTOs had a null carrierBookingReference while another did not");
        }
        allNull = Boolean.TRUE;
        continue;
      }

      if (allNull == Boolean.TRUE) {
        throw new NullPointerException(
            "One of the CargoItemTOs had a null carrierBookingReference while another did not");
      }
      allNull = Boolean.FALSE;
      if (!cargoBookingReference.equals(possibleCentralBookingReference)) {
        possibleCentralBookingReference = null;
        break;
      }
    }
    if (actualCentralBookingReference != null
        && !actualCentralBookingReference.equals(possibleCentralBookingReference)) {
      throw new IllegalStateException(
          "Internal error: ShippingInstruction had booking reference "
              + this.getCarrierBookingReference()
              + " but it should have been: "
              + actualCentralBookingReference);
    }
    if (possibleCentralBookingReference != null) {
      // Hoist up the booking reference to the SI since it is the same on all items.
      for (ShipmentEquipmentTO shipmentEquipmentTO : shipmentEquipments) {
        shipmentEquipmentTO.setCarrierBookingReference(null);
      }
      this.setCarrierBookingReference(possibleCentralBookingReference);
    }
  }

  /**
   * Pushes the carrierBookingReference to shipmentEquipment and clears it if it is not null
   *
   * <p>This is useful on input to "normalize" the ShippingInstruction so the code can always assume
   * that the booking reference will appear on the ShipmentEquipment. The method is idempotent.
   *
   * <p>This is more or less the logical opposite of {@link
   * #hoistCarrierBookingReferenceIfPossible()}.
   *
   * @throws IllegalStateException If the ShippingInstruction and one of its ShipmentEquipment both
   *     have a non-null carrierBookingReference.
   */
  @JsonIgnore
  public void pushCarrierBookingReferenceIntoShipmentEquipmentIfNecessary() {
    if (this.carrierBookingReference != null && this.shipmentEquipments == null) return;

    if (this.carrierBookingReference == null
        && (this.shipmentEquipments == null
            || this.shipmentEquipments.stream()
                .allMatch(
                    shipmentEquipmentTO ->
                        shipmentEquipmentTO.getCarrierBookingReference() == null))) {
      throw new IllegalStateException(
          "CarrierBookingReference needs to be defined on either ShippingInstruction or ShipmentEquipmentTO level.");
    }

    String centralBookingReference = this.getCarrierBookingReference();
    if (centralBookingReference != null) {
      for (ShipmentEquipmentTO shipmentEquipmentTO : this.shipmentEquipments) {
        String ShipmentEquipmentBookingReference = shipmentEquipmentTO.getCarrierBookingReference();
        if (ShipmentEquipmentBookingReference != null) {
          throw new IllegalStateException(
              "CarrierBookingReference defined on both ShippingInstruction and ShipmentEquipmentTO level.");
        }
        shipmentEquipmentTO.setCarrierBookingReference(centralBookingReference);
      }
      this.setCarrierBookingReference(null);
    }
  }
}
