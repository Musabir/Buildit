package com.rentit.sales.domain.repository;

import com.rentit.reservation.domain.model.PlantReservation;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


public class PurchaseOrderRepositoryImpl implements CustomPurchaseOrderRepository {

    @Autowired
    EntityManager em;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updatePurchaseOrderById(String id, PlantReservation plantReservation) {
        em.createQuery("UPDATE PurchaseOrder p SET p.reservation = ?1 WHERE p.id = ?2")
                .setParameter(1,plantReservation)
                .setParameter(2,id).executeUpdate();
    }

    public List<PurchaseOrder> getPurchaseOrderByStatus(PurchaseOrderStatus purchaseOrderStatus) {
        return em.createQuery("SELECT p FROM PurchaseOrder p WHERE p.status = ?1", PurchaseOrder.class)
                .setParameter(1,purchaseOrderStatus).getResultList();
    }

    @Override
    public PurchaseOrder getPurchaseOrderReservationsID(String reservationId) {
        return em.createQuery("SELECT p FROM PurchaseOrder p WHERE p.reservation.id = ?1", PurchaseOrder.class)
                .setParameter(1,reservationId).getSingleResult();
    }

}
