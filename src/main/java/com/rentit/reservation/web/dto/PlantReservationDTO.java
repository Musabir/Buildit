package com.rentit.reservation.web.dto;

import com.rentit.common.application.dto.BusinessPeriodDTO;
import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.inventory.domain.model.PlantInventoryItem;
import com.rentit.sales.domain.model.PurchaseOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.rentit.common.rest.ResourceSupport;



@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode(callSuper = false)
public class PlantReservationDTO extends ResourceSupport {

    String _id;
    BusinessPeriodDTO schedule;

    PlantInventoryItem plant;

}
