package com.rentit.inventory.domain.repository;

import com.rentit.inventory.domain.model.PlantInventoryEntry;

import java.time.LocalDate;
import java.util.List;

public interface CustomPlantInventoryEntryRepository {
    List<PlantInventoryEntry> findAvailableByName(String name, LocalDate startDate, LocalDate endDate);
    PlantInventoryEntry findAvailableById(String id, LocalDate startDate, LocalDate endDate);
    List<PlantInventoryEntry> findByNameContaining(String substring);
}
