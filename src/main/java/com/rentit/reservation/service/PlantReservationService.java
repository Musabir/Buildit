package com.rentit.reservation.service;

import com.rentit.common.application.dto.BusinessPeriodDTO;
import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.common.infrastructure.IdentifierFactory;
import com.rentit.exception.PlantNotFoundException;
import com.rentit.exception.PlantUnavailableException;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.inventory.domain.repository.PlantInventoryEntryRepository;
import com.rentit.inventory.domain.repository.PlantInventoryItemRepository;
import com.rentit.inventory.web.dto.PlantInventoryEntryDTO;
import com.rentit.reservation.domain.model.PlantReservation;
import com.rentit.reservation.domain.repository.PlantReservationRepository;
import com.rentit.reservation.web.dto.PlantReservationDTO;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.repository.PurchaseOrderRepository;
import org.h2.table.Plan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlantReservationService {

    @Autowired
    IdentifierFactory identifierFactory;

    @Autowired
    PlantReservationAssembler plantReservationAssembler;

    @Autowired
    PlantInventoryItemRepository plantInventoryItemRepository;

    @Autowired
    PlantReservationRepository plantReservationRepository;

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;


    public PlantReservationDTO createPlantReservation(PlantInventoryEntryDTO plantInventoryEntryDTO, BusinessPeriod businessPeriod) throws PlantUnavailableException {

        List<PlantInventoryItem> plantInventoryItemList = plantInventoryItemRepository.findByAvailablity(
                PlantInventoryEntry.of(
                        plantInventoryEntryDTO.get_id(),
                        plantInventoryEntryDTO.getName(),
                        plantInventoryEntryDTO.getDescription(),
                        plantInventoryEntryDTO.getPrice()),
                businessPeriod.getStartDate(),
                businessPeriod.getEndDate());

        if (plantInventoryItemList.size() == 0) {
            throw new PlantUnavailableException();
        } else {
            PlantReservation plantReservation = PlantReservation.of(
                    identifierFactory.nextDomainObjectID(),
                    businessPeriod,
                    plantInventoryItemList.get(0)
            );
            PlantReservation createdResrvation = plantReservationRepository.save(plantReservation);
            return plantReservationAssembler.toResource(createdResrvation);
        }
    }

}
