package com.rentit.sales.domain.model;

import com.rentit.common.domain.model.BusinessPeriod;
import com.rentit.inventory.domain.model.PlantInventoryEntry;
import com.rentit.reservation.domain.model.PlantReservation;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(force=true,access= AccessLevel.PROTECTED)
@AllArgsConstructor(staticName="of")
public class PurchaseOrder {
    @Id
    String id;

    LocalDate issueDate;
    LocalDate paymentSchedule;

    BigDecimal total;

    @Enumerated(EnumType.STRING)
    PurchaseOrderStatus status;



    @OneToOne
    PlantReservation reservation;



    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    List<PurchaseOrderExtension> extensions;
    @Embedded
    PlantInventoryEntry plant;
//    @ManyToMany
//    List<PlantInventoryEntry> plants;


    @Embedded
    BusinessPeriod rentalPeriod;

    String consumerEmail;




    public static PurchaseOrder of(String id, String email, BusinessPeriod rentalPeriod, PlantInventoryEntry plant) {
        PurchaseOrder po = new PurchaseOrder();
        po.id = id;
        po.plant = plant;
        po.consumerEmail = email;
        po.rentalPeriod = rentalPeriod;
        po.status = PurchaseOrderStatus.CREATED;
        return po;
    }

    public void confirmReservation(PlantReservation plantReservation, BigDecimal price) {
        reservation = plantReservation;
        rentalPeriod = plantReservation.getSchedule();
        total = price.multiply(BigDecimal.valueOf(plantReservation.getSchedule().numberOfWorkingDays()));
        status = PurchaseOrderStatus.OPEN;
    }

    public void handleRejection() {
        status = PurchaseOrderStatus.REJECTED;
    }

    public void cancelPurchaseOrder() {
        status = PurchaseOrderStatus.CANCEL;
    }
    public void cancelConfirmedPurchaseOrder() {
        status = PurchaseOrderStatus.CANCEL_PENDING;
    }
    public void handleResubmiting(BusinessPeriod businessPeriod) {
        status = PurchaseOrderStatus.PENDING_WORKER_CONFIRM;
        rentalPeriod = businessPeriod;

    }



    public void handleAcceptance() {
        status = PurchaseOrderStatus.OPEN;
    }


    public void addExtension(PurchaseOrderExtension purchaseOrderExtension){
        status = PurchaseOrderStatus.PENDING_EXTENSION;
        extensions.add(purchaseOrderExtension);
    }
}
