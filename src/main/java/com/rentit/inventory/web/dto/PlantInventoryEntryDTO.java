package com.rentit.inventory.web.dto;

import com.rentit.common.rest.ResourceSupport;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode(callSuper = false)
public class PlantInventoryEntryDTO extends ResourceSupport {
    String _id;
    String name;
    String description;
    BigDecimal price;

    public PlantInventoryEntry convertToEntity(String UUID) {
        return PlantInventoryEntry.of(
                UUID,
                name,
                description,
                price
        );
    }
}