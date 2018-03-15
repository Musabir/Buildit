package com.rentit.sales.domain.repository;

import com.rentit.sales.domain.model.PurchaseOrderExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurchaseOrderExtensionRepository extends JpaRepository<PurchaseOrderExtension, String> {
}
