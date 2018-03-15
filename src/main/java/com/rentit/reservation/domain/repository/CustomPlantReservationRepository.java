package com.rentit.reservation.domain.repository;

import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.reservation.domain.model.PlantReservation;


import java.util.List;

public interface CustomPlantReservationRepository {

    public List<PlantReservation> findReservationByItemAndPeriod(String plantInventoryItemID, BusinessPeriod businessPeriod);
}
