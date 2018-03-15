package com.rentit.sales.web.dto;

import com.rentit.common.application.dto.BusinessPeriodDTO;
import com.rentit.inventory.web.dto.PlantInventoryEntryDTO;
import com.rentit.sales.domain.model.PurchaseOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.rentit.common.rest.ResourceSupport;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode(callSuper = false)
public class PurchaseOrderDTO extends ResourceSupport{
    String _id;
    LocalDate issueDate;
    LocalDate paymentSchedule;
    BigDecimal total;
    String consumerEmail;
    PurchaseOrderStatus status;
    PlantInventoryEntryDTO plant;
    BusinessPeriodDTO rentalPeriod;



}
