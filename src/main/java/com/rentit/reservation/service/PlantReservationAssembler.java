package com.rentit.reservation.service;

import com.rentit.common.application.dto.BusinessPeriodDTO;
import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.reservation.domain.model.PlantReservation;
import com.rentit.reservation.web.controller.PlantReservationRestController;
import com.rentit.reservation.web.dto.PlantReservationDTO;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Service;


@Service
public class PlantReservationAssembler extends ResourceAssemblerSupport<PlantReservation, PlantReservationDTO>
{

    public PlantReservationAssembler() {
        super(PlantReservationRestController.class, PlantReservationDTO.class);
    }

    @Override
    public PlantReservationDTO toResource(PlantReservation plantReservation) {
        PlantReservationDTO dto = createResourceWithId(plantReservation.getId(), plantReservation);
        dto.set_id(plantReservation.getId());
//        dto.setMaint_plan(plantReservation.getMaint_plan());
        BusinessPeriodDTO businessPeriodDTO = new BusinessPeriodDTO();
        businessPeriodDTO.setStartDate(plantReservation.getSchedule().getStartDate());
        businessPeriodDTO.setEndDate(plantReservation.getSchedule().getEndDate());
        dto.setPlant(plantReservation.getPlant());
//        dto.setRental(plantReservation.getRental());
        dto.setSchedule(businessPeriodDTO);
        return dto;
    }
}