package org.dcsa.core.events.model.transferobjects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.edocumentation.model.transferobject.ConsignmentItemTO;
import org.dcsa.core.events.edocumentation.model.transferobject.ShipmentTO;
import org.dcsa.core.events.model.base.AbstractShippingInstruction;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ShippingInstructionTO extends AbstractShippingInstruction {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String carrierBookingReference;

  @Valid
  private LocationTO placeOfIssue;

  @Valid
  @NotEmpty
  private List<UtilizedTransportEquipmentTO> utilizedTransportEquipments;

  @Valid private List<DocumentPartyTO> documentParties;

  @Valid private List<ReferenceTO> references;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  private List<ShipmentTO> shipments;

  // @NotEmpty
  @Valid
  private List<ConsignmentItemTO> consignmentItems;

  /**
   * Pull the carrierBookingReference from utilizedTransportEquipments into the ShippingInstruction if
   * possible
   *
   * <p>If the UtilizedTransportEquipments all have the same carrierBookingReference, the value is moved up
   * to this ShippingInstruction and cleared from the UtilizedTransportEquipments.
   *
   * <p>This is useful on output to "prettify" the resulting ShippingInstruction to avoid
   * unnecessary "per UtilizedTransportEquipment" carrier booking references. The method is idempotent.
   *
   * <p>This is more or less the logical opposite of {@link
   * #pushCarrierBookingReferenceIntoUtilizedTransportEquipmentIfNecessary()}.
   *
   * @throws IllegalStateException If the ShippingInstruction already has a carrierBookingReference
   *     and it is not exactly the same as it would get after this call.
   * @throws NullPointerException If one or more UtilizedTransportEquipmentTO had a null
   *     carrierBookingReference AND one or more of them had a non-null carrierBookingReference.
   *     (I.e. either they all must have a carrierBookingReference or none of them can have one).
   */
  @JsonIgnore
  public void hoistCarrierBookingReferenceIfPossible() {

    String actualCentralBookingReference = this.getCarrierBookingReference();
    String possibleCentralBookingReference =
        utilizedTransportEquipments.isEmpty()
            ? null
            : utilizedTransportEquipments.get(0).getCarrierBookingReference();
    Boolean allNull = null;
    for (UtilizedTransportEquipmentTO utilizedTransportEquipmentTO : utilizedTransportEquipments) {
      String cargoBookingReference = utilizedTransportEquipmentTO.getCarrierBookingReference();
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
      for (UtilizedTransportEquipmentTO utilizedTransportEquipmentTO : utilizedTransportEquipments) {
        utilizedTransportEquipmentTO.setCarrierBookingReference(null);
      }
      this.setCarrierBookingReference(possibleCentralBookingReference);
    }
  }

  /**
   * Pushes the carrierBookingReference to utilizedTransportEquipment and clears it if it is not null
   *
   * <p>This is useful on input to "normalize" the ShippingInstruction so the code can always assume
   * that the booking reference will appear on the UtilizedTransportEquipment. The method is idempotent.
   *
   * <p>This is more or less the logical opposite of {@link
   * #hoistCarrierBookingReferenceIfPossible()}.
   *
   * @throws IllegalStateException If the ShippingInstruction and one of its UtilizedTransportEquipment both
   *     have a non-null carrierBookingReference.
   */
  @JsonIgnore
  public void pushCarrierBookingReferenceIntoUtilizedTransportEquipmentIfNecessary() {
    if (this.carrierBookingReference != null && this.utilizedTransportEquipments == null && this.consignmentItems == null) return;

    if (this.carrierBookingReference == null
        && (this.utilizedTransportEquipments == null
            || this.utilizedTransportEquipments.stream()
                .allMatch(utilizedTransportEquipmentTO -> utilizedTransportEquipmentTO.getCarrierBookingReference() == null))
        && (this.consignmentItems == null
            || this.consignmentItems.stream()
                .allMatch(consignmentItemTO -> consignmentItemTO.getCarrierBookingReference() == null))
    ) {
      throw new IllegalStateException(
          "CarrierBookingReference needs to be defined on either ShippingInstruction, UtilizedTransportEquipment or ConsignmentItem level.");
    }

    String centralBookingReference = this.getCarrierBookingReference();
    if (centralBookingReference != null) {
      if (utilizedTransportEquipments != null) {
        for (UtilizedTransportEquipmentTO utilizedTransportEquipmentTO : this.utilizedTransportEquipments) {
          String utilizedTransportEquipmentBookingReference = utilizedTransportEquipmentTO.getCarrierBookingReference();
          if (utilizedTransportEquipmentBookingReference != null) {
            throw new IllegalStateException(
                "CarrierBookingReference defined on both ShippingInstruction and UtilizedTransportEquipment level.");
          }
          utilizedTransportEquipmentTO.setCarrierBookingReference(centralBookingReference);
        }
      }
      if (consignmentItems != null) {
        consignmentItems =
            consignmentItems.stream()
                .map(
                    consignmentItemTO -> {
                      if (consignmentItemTO.getCarrierBookingReference() != null) {
                        throw new IllegalStateException(
                            "CarrierBookingReference defined on both ShippingInstruction and ConsignmentItem level.");
                      }
                      return consignmentItemTO.withCarrierBookingReference(centralBookingReference);
                    })
                .collect(Collectors.toList());
      }
    }
    this.setCarrierBookingReference(null);
  }
}
