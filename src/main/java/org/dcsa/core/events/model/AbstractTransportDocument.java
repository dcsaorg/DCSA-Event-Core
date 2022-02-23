package org.dcsa.core.events.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dcsa.core.model.AuditBase;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public abstract class AbstractTransportDocument extends AuditBase {

    @Id
    @Column("transport_document_reference")
    @Size(max = 20)
    private String transportDocumentReference;

    @Column("issue_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate issueDate;

    @Column("shipped_onboard_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate shippedOnBoardDate;

    @Column("received_for_shipment_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate receivedForShipmentDate;

    @Column("number_of_originals")
    private Integer numberOfOriginals;

    @Column("issuer")
    @Size(max = 4)
    private String issuer;

    @Column("shipping_instruction_id")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String shippingInstructionID;

    @Column("declared_value_currency")
    @Size(max = 3)
    private String declaredValueCurrency;

    @Column("declared_value")
    private Float declaredValue;

    @Column("number_of_rider_pages")
    private Integer numberOfRiderPages;
}