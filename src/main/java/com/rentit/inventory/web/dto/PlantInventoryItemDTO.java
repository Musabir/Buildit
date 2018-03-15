package com.rentit.inventory.web.dto;

import com.rentit.common.rest.ResourceSupport;
import com.rentit.inventory.domain.model.EquipmentCondition;
import com.rentit.inventory.domain.model.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode(callSuper = false)
public class PlantInventoryItemDTO extends ResourceSupport{
    String _id;
    String serialNumber;
    EquipmentCondition equipmentCondition;
    ItemStatus itemStatus;
    PlantInventoryEntryDTO plantInfo;

}
