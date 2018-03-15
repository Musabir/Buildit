package com.rentit.inventory.service;

import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.exception.PlantNotFoundException;
import com.rentit.inventory.domain.model.EquipmentCondition;
import com.rentit.inventory.domain.model.ItemStatus;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.inventory.domain.repository.PlantInventoryEntryRepository;
import com.rentit.common.infrastructure.IdentifierFactory;
import com.rentit.inventory.domain.repository.PlantInventoryItemRepository;
import com.rentit.inventory.web.dto.PlantInventoryEntryDTO;
import com.rentit.inventory.web.dto.PlantInventoryItemDTO;
import com.rentit.reservation.web.dto.PlantReservationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {

    @Autowired
    IdentifierFactory identifierFactory;

    @Autowired
    PlantInventoryEntryRepository plantInventoryEntryRepository;

    @Autowired
    PlantInventoryEntryAssembler plantInventoryEntryAssembler;

    @Autowired
    PlantInventoryItemAssembler plantInventoryItemAssembler;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    public void createInventoryEntry(PlantInventoryEntryDTO plantInventoryEntryDTO) {
        PlantInventoryEntry plantInventoryEntry = PlantInventoryEntry.of(
                identifierFactory.nextDomainObjectID(),
                plantInventoryEntryDTO.getName(),
                plantInventoryEntryDTO.getDescription(),
                plantInventoryEntryDTO.getPrice()
        );

        plantInventoryEntryRepository.save(plantInventoryEntry);
    }


    public List<PlantInventoryEntryDTO> findAllInventoryEntries() {

        List<PlantInventoryEntry> plantInventoryEntries = plantInventoryEntryRepository.findAll();
        List<PlantInventoryEntryDTO> plantInventoryEntryDTOs =  new ArrayList<PlantInventoryEntryDTO>();

        plantInventoryEntries.forEach(plantInventoryEntry -> {
            plantInventoryEntryDTOs.add(plantInventoryEntryAssembler.toResource(plantInventoryEntry));
        });
        return plantInventoryEntryDTOs;
    }

    public List<PlantInventoryEntryDTO> findAvailablePlantsRest(String name, LocalDate startDate, LocalDate endDate) {
        List<PlantInventoryEntry> plantInventoryEntryList = plantInventoryEntryRepository.findAvailableByName(name, startDate, endDate);
        List<PlantInventoryEntryDTO> plantInventoryEntryDTOList =  new ArrayList<PlantInventoryEntryDTO>();
        for(PlantInventoryEntry plantInventoryEntry: plantInventoryEntryList){
            plantInventoryEntryDTOList.add(plantInventoryEntryAssembler.toResource(plantInventoryEntry));
        }
        return plantInventoryEntryDTOList;
    }

    public void updateInventoryItemConditionByEntryId(String entryId, EquipmentCondition newEq, EquipmentCondition oldEq) {
        plantInventoryItemRepository.updateInventoryItemConditionByEntryId(entryId,newEq,oldEq);
    }

    public void updateInventoryItemCondition(PlantInventoryItem plantInventoryItem, EquipmentCondition eq) {
        plantInventoryItemRepository.updateInventoryItemCondition(plantInventoryItem,eq);
    }

    public List<PlantInventoryItemDTO> findInventoryItemByEntryIdByCondition(String entryId, EquipmentCondition eq){
        List<PlantInventoryItem> plantInventoryItems = plantInventoryItemRepository.findInventoryItemByEntryIdByCondition(entryId,eq);
        List<PlantInventoryItemDTO> plantInventoryItemDTOs =  new ArrayList<PlantInventoryItemDTO>();

        plantInventoryItems.forEach(plantInventoryItem -> {
            plantInventoryItemDTOs.add(plantInventoryItemAssembler.toResource(plantInventoryItem));
        });
        return plantInventoryItemDTOs;
    }

    public PlantInventoryEntryDTO findPlant(String id) {
        return plantInventoryEntryAssembler.toResource(plantInventoryEntryRepository.findOne(id));
    }

    public PlantInventoryItemDTO findInvntoryItemByID(String id){
        return plantInventoryItemAssembler.toResource(plantInventoryItemRepository.findOne(id));
    }

    public PlantInventoryItemDTO updatePlantItem(PlantInventoryItemDTO po) {
        return plantInventoryItemAssembler.toResource(plantInventoryItemRepository.save(PlantInventoryItem.of(
                po.get_id(),
                po.getSerialNumber(),
                po.getEquipmentCondition(),
                po.getItemStatus(),
                po.getPlantInfo().convertToEntity(po.getPlantInfo().get_id())
        )));
    }
}
