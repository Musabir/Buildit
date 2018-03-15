package com.rentit.inventory.domain.repository;

import com.rentit.inventory.domain.model.EquipmentCondition;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.domain.model.PlantInventoryItem;

import java.time.LocalDate;
import java.util.List;


public interface CustomPlantInventoryItemRepository {
    List<PlantInventoryItem> findPlantItemsNotHiredBetween(LocalDate startDate, LocalDate endDate);
    List<PlantInventoryItem> findByAvailablity(PlantInventoryEntry pie, LocalDate startDate, LocalDate endDate);
    List<PlantInventoryItem> findByAvailablityById(String id, LocalDate startDate, LocalDate endDate);
    void updateInventoryItemConditionByEntryId(String entry_id, EquipmentCondition new_eq, EquipmentCondition old_eq);
    void updateInventoryItemCondition(PlantInventoryItem plantInventoryItem, EquipmentCondition eq);
    List<PlantInventoryItem> findInventoryItemByEntryIdByCondition(String entry_id, EquipmentCondition new_eq);
}
