package com.rentit.sales.domain.repository;

import com.rentit.reservation.domain.model.PlantReservation;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderStatus;

import java.util.List;


public interface CustomPurchaseOrderRepository {
    void updatePurchaseOrderById(String id, PlantReservation plantReservation);
    List<PurchaseOrder> getPurchaseOrderByStatus(PurchaseOrderStatus purchaseOrderStatus);
    PurchaseOrder getPurchaseOrderReservationsID(String reservationId);

}



