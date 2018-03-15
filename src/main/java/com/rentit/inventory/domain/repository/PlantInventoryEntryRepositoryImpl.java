package com.rentit.inventory.domain.repository;

import com.rentit.inventory.domain.model.PlantInventoryEntry;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;


public class PlantInventoryEntryRepositoryImpl implements CustomPlantInventoryEntryRepository {
    @Autowired
    EntityManager em;

    public List<PlantInventoryEntry> findByNameContaining(String substring) {
        return em.createQuery("select e from PlantInventoryEntry e where lower(e.name) like ?1", PlantInventoryEntry.class)
                .setParameter(1, "%"+substring.toLowerCase()+"%")
                .getResultList();
    }


//    public List<PlantInventoryItem> findByAvailablity(PlantInventoryEntry pie) {
//        return em.createQuery("SELECT i FROM PlantInventoryItem i" +
//                "  WHERE i.plantInfo = ?1 AND i.equipmentCondition = ?2 AND " +
//                "  NOT EXISTS (SELECT 1 FROM PlantReservation r " +
//                "  WHERE i.id = r.plant AND ?3 BETWEEN r.schedule.startDate AND  r.schedule.endDate)", PlantInventoryItem.class)
//                .setParameter(1,pie)
//                .setParameter(2,EquipmentCondition.SERVICEABLE)
//                .setParameter(3,LocalDate.now())
//                .getResultList();
//    }

    @Override
    public List<PlantInventoryEntry> findAvailableByName(String name, LocalDate startDate, LocalDate endDate) {
        System.out.println("Servisable");

        return em.createQuery("select e from PlantInventoryItem i join i.plantInfo e where i.equipmentCondition = 'SERVICEABLE' AND lower(e.name) like :name" +
                " AND NOT EXISTS (select r from PlantReservation r where r.plant = i and :startDate <= r.schedule.endDate and :endDate >= r.schedule.endDate)", PlantInventoryEntry.class)
                .setParameter("name", "%"+ name.toLowerCase() +"%")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public PlantInventoryEntry findAvailableById(String id, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select e from PlantInventoryItem i join i.plantInfo e where e.id = :id " +
                " AND NOT EXISTS (select r from PlantReservation r where r.plant = i and :startDate <= r.schedule.endDate and :endDate >= r.schedule.startDate)", PlantInventoryEntry.class)
                .setParameter("id", id)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();
    }
//    UNSERVICEABLE_INCOMPLETE


    



}
