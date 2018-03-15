package com.rentit.inventory.domain.repository;

import com.rentit.inventory.domain.model.EquipmentCondition;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.reservation.domain.model.PlantReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class PlantInventoryItemRepositoryImpl implements CustomPlantInventoryItemRepository {
    @Autowired
    EntityManager em;

    @Override
    public List<PlantInventoryItem> findPlantItemsNotHiredBetween(LocalDate startDate, LocalDate endDate) {
        return em.createQuery("select pr.plant from PlantReservation pr where not exists " +
                "(select po from PurchaseOrder po where po.status = 'CLOSED' and " +
                "(pr.schedule.endDate between :startDate and :endDate))")
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();
    }

    @Override
    public List<PlantInventoryItem> findByAvailablity(PlantInventoryEntry pie, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("SELECT i FROM PlantInventoryItem i" +
                "  WHERE i.plantInfo = ?1 AND i.equipmentCondition = ?2 AND " +
                "  NOT EXISTS (SELECT 1 FROM PlantReservation r " +
                "  WHERE i.id = r.plant AND  ?3 <= r.schedule.endDate AND ?4 >= r.schedule.startDate)", PlantInventoryItem.class)
                .setParameter(1,pie)
                .setParameter(2, EquipmentCondition.SERVICEABLE)
                .setParameter(3,startDate)
                .setParameter(4,endDate)
                .getResultList();
    }




    @Override
    public List<PlantInventoryItem> findByAvailablityById(String id, LocalDate startDate, LocalDate endDate) {
        return em.createQuery("SELECT i FROM PlantInventoryItem i" +
                "  WHERE i.id = ?1 AND i.equipmentCondition = ?2 AND " +
                "  NOT EXISTS (SELECT 1 FROM PlantReservation r " +
                "  WHERE i.id = r.plant AND  ?3 <= r.schedule.endDate AND ?4 >= r.schedule.startDate)", PlantInventoryItem.class)
                .setParameter(1,id)
                .setParameter(2, EquipmentCondition.SERVICEABLE)
                .setParameter(3,startDate)
                .setParameter(4,endDate)
                .getResultList();
    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateInventoryItemConditionByEntryId(String entryId, EquipmentCondition newEq, EquipmentCondition oldEq){
        em.createQuery(" UPDATE PlantInventoryItem i SET i.equipmentCondition = ?1 WHERE i.plantInfo.id = ?2 AND i.equipmentCondition  = ?3 LIMIT 1 ")
                .setParameter(1,newEq)
                .setParameter(2,entryId)
                .setParameter(3,oldEq).executeUpdate();
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateInventoryItemCondition(PlantInventoryItem plantInventoryItem, EquipmentCondition eq){
        em.createQuery(" UPDATE PlantInventoryItem i SET i.equipmentCondition = ?1 WHERE i.id = ?2 ")
                .setParameter(1,eq)
                .setParameter(2,plantInventoryItem.getId()).executeUpdate();
    }

    @Override
    public List<PlantInventoryItem> findInventoryItemByEntryIdByCondition(String entryId, EquipmentCondition eq){
        return em.createQuery(" SELECT i FROM PlantInventoryItem i WHERE i.plantInfo.id = ?1 AND i.equipmentCondition  = ?2 ", PlantInventoryItem.class)
                .setParameter(1,entryId)
                .setParameter(2,eq).getResultList();
    }
}
