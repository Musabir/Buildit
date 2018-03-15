package com.rentit.reservation.domain.repository;

import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.reservation.domain.model.PlantReservation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.util.List;


public class PlantReservationRepositoryImpl implements CustomPlantReservationRepository {


    @Autowired
    EntityManager em;


    @Override
    public List<PlantReservation> findReservationByItemAndPeriod(String plantInventoryItemID, BusinessPeriod businessPeriod) {

        return em.createQuery("SELECT r FROM PlantReservation r " +
                "  WHERE r.plant.id = ?1 AND  ?2 <= r.schedule.endDate AND ?3 >= r.schedule.startDate ", PlantReservation.class)
                .setParameter(1, plantInventoryItemID)
                .setParameter(2, businessPeriod.getStartDate())
                .setParameter(3, businessPeriod.getEndDate())
                .getResultList();

    }
}
