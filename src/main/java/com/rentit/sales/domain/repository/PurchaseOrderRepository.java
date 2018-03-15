package com.rentit.sales.domain.repository;

import com.rentit.sales.domain.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, String>, CustomPurchaseOrderRepository {
}
