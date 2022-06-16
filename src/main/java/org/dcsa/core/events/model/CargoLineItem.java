package org.dcsa.core.events.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.dcsa.core.events.model.base.AbstractCargoLineItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@NoArgsConstructor
@Data
@Table("cargo_line_item")
public class CargoLineItem extends AbstractCargoLineItem {

    @Id
    private UUID id;  /* TODO: Remove */

    @Column("cargo_item_id")
    private UUID cargoItemID;

}
