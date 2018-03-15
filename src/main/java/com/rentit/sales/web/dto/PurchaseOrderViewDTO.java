package com.rentit.sales.web.dto;

import com.rentit.inventory.domain.model.ItemStatus;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.inventory.web.dto.PlantInventoryItemDTO;
import com.rentit.reservation.domain.model.PlantReservation;
import com.rentit.reservation.web.dto.PlantReservationDTO;
import lombok.Data;

@Data
public class PurchaseOrderViewDTO {
    PurchaseOrderDTO purchaseOrderDTO;
    PlantInventoryItemDTO plantInventoryItemDTO;
}
