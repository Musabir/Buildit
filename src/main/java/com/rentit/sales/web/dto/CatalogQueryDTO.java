package com.rentit.sales.web.dto;

import com.rentit.common.application.dto.BusinessPeriodDTO;
import lombok.*;

@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor(staticName="of")
public class CatalogQueryDTO {
    String name;
    BusinessPeriodDTO rentalPeriod;
}