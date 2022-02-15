package org.dcsa.core.events.edocumentation.model.transferobject;

import lombok.Data;
import org.dcsa.core.events.model.enums.PaymentTerm;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public
class ChargeTO {

  @NotNull(message = "ChargeTypeCode is required.")
  @Size(max = 20, message = "ChargeTypeCode has a max size of 20.")
  private String chargeTypeCode;

  @NotNull(message = "CurrencyAmount is required.")
  private Double currencyAmount;

  @NotNull(message = "CurrencyCode is required.")
  @Size(max = 3, message = "CurrencyCode has a max size of 3.")
  private String currencyCode;

  @NotNull(message = "PaymentTermCode is required.")
  private PaymentTerm paymentTermCode;

  @NotNull(message = "CalculationBasis is required.")
  @Size(max = 50, message = "CalculationBasis has a max size of 50.")
  private String calculationBasis;

  @NotNull(message = "UnitPrice is required.")
  private Double unitPrice;

  @NotNull(message = "Quantity is required.")
  private Double quantity;
}
