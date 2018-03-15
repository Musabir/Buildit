package com.rentit.common.application.dto;

import com.rentit.common.domain.model.BusinessPeriod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.ResourceSupport;

import java.time.LocalDate;

@Data
@NoArgsConstructor(force=true)
@AllArgsConstructor(staticName="of")
@EqualsAndHashCode(callSuper = false)
public class BusinessPeriodDTO extends ResourceSupport {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate endDate;

    public BusinessPeriod convertToEntity() {
        return BusinessPeriod.of(
                startDate,
                endDate
        );
    }
}
