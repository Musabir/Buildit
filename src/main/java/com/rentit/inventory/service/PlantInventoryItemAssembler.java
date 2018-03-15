package com.rentit.inventory.service;

import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.inventory.web.dto.PlantInventoryItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;

@Service
public class PlantInventoryItemAssembler extends ResourceAssemblerSupport<PlantInventoryItem,PlantInventoryItemDTO> {
    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    public PlantInventoryItemAssembler() {
        super(PlantInventoryItem.class,PlantInventoryItemDTO.class);
    }

    @Override
    public PlantInventoryItemDTO toResource(PlantInventoryItem plantInventoryItem) {
        PlantInventoryItemDTO plantInventoryItemDTO = createResourceWithId(plantInventoryItem.getId(),plantInventoryItem);
        plantInventoryItemDTO.set_id(plantInventoryItem.getId());
        plantInventoryItemDTO.setEquipmentCondition(plantInventoryItem.getEquipmentCondition());
        plantInventoryItemDTO.setPlantInfo(plantInventoryEntryAssembler.toResource(plantInventoryItem.getPlantInfo()));
        plantInventoryItemDTO.setItemStatus(plantInventoryItem.getItemStatus());
        plantInventoryItemDTO.setSerialNumber(plantInventoryItem.getSerialNumber());
        return plantInventoryItemDTO;
    }
}
