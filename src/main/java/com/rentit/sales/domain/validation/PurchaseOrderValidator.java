package com.rentit.sales.domain.validation;

import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.sales.domain.model.PurchaseOrder;
import com.rentit.sales.domain.model.PurchaseOrderStatus;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PurchaseOrderValidator implements Validator{

    public PurchaseOrderValidator(){

    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PurchaseOrder.class.isAssignableFrom(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        PurchaseOrder order = (PurchaseOrder)o;
        if (order.getId() == null)
            errors.reject("id", "Purchase Order id can't be null");
        if (order.getPlant() == null)
            errors.reject("plant", "Reference to PlantInventoryEntry can't be null");
        if (!order.getStatus().equals(PurchaseOrderStatus.PENDING_WORKER_CONFIRM)) {
            if (order.getReservation() != null)
                errors.reject("reservation", "PlantReservation is required");
            if (order.getTotal() == null)
                errors.rejectValue("total", "total can't be null");
            else if (order.getTotal().signum() != 1)
                errors.rejectValue("total", "total must be a positive value");
        }


        BusinessPeriod period = (BusinessPeriod) order.getRentalPeriod();

        if (period.getStartDate() == null)
            errors.rejectValue("startDate", "Start date of Business Period can't be NULL");

        if (period.getStartDate() == null)
            errors.rejectValue("endDate", "End date of Business Period can't be NULL");

        if (period.getEndDate().isBefore(period.getStartDate())) {
            errors.rejectValue("startDate", "Start date happens after end date'");
            errors.rejectValue("endDate", "'End date happens before start date'");
        }

        if (order.getConsumerEmail() == null)
            errors.rejectValue("email", "'email' in Contact Info cannot be NULL");

    }
}
