package org.dcsa.core.events.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.base.AbstractCargoItem;
import org.dcsa.core.model.GetId;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Table("cargo_item")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CargoItem extends AbstractCargoItem implements GetId<UUID> {

  @Id private UUID id;

  @Column("shipment_id")
  @NotNull
  private UUID shipmentID;

  @Column("shipping_instruction_id")
  private String shippingInstructionID;

  @Column("shipment_equipment_id")
  @NotNull
  protected UUID shipmentEquipmentID;

//  public CargoItemTO toCargoItemTO() {
//    CargoItemTO cargoItemTO = new CargoItemTO();
//    cargoItemTO.setHsCode(this.getHsCode());
//    cargoItemTO.setVolume(this.getVolume());
//    cargoItemTO.setVolumeUnit(this.getVolumeUnit());
//    cargoItemTO.setWeight(this.getWeight());
//    cargoItemTO.setWeightUnit(this.getWeightUnit());
//    cargoItemTO.setDescriptionOfGoods(this.getDescriptionOfGoods());
//    cargoItemTO.setPackageCode(this.getPackageCode());
//    cargoItemTO.setNumberOfPackages(this.getNumberOfPackages());
//    return cargoItemTO;
//  }

//  public CargoItemTO toCargoItemTO(
//      List<CargoLineItem> cargoLineItems,
//      List<Reference> references,
//      String carrierBookingRequestReference) {
//    CargoItemTO cargoItemTO = new CargoItemTO();
//    cargoItemTO.setCargoLineItems(
//        cargoLineItems.stream()
//            .map(cargoLineItem -> cargoLineItem.toCargoLineItemTO())
//            .collect(Collectors.toList()));
//    cargoItemTO.setReferences(
//        references.stream()
//            .map(reference -> reference.toReferenceTO())
//            .collect(Collectors.toList()));
//    cargoItemTO.setCarrierBookingReference(carrierBookingRequestReference);
//    cargoItemTO.setHsCode(this.getHsCode());
//    cargoItemTO.setVolume(this.getVolume());
//    cargoItemTO.setVolumeUnit(this.getVolumeUnit());
//    cargoItemTO.setWeight(this.getWeight());
//    cargoItemTO.setWeightUnit(this.getWeightUnit());
//    cargoItemTO.setDescriptionOfGoods(this.getDescriptionOfGoods());
//    cargoItemTO.setPackageCode(this.getPackageCode());
//    cargoItemTO.setNumberOfPackages(this.getNumberOfPackages());
//    return cargoItemTO;
//  }
}
