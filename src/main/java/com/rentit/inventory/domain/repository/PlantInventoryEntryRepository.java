package com.rentit.inventory.domain.repository;

import com.rentit.inventory.domain.model.PlantInventoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PlantInventoryEntryRepository extends JpaRepository<PlantInventoryEntry, String>, CustomPlantInventoryEntryRepository {
}
