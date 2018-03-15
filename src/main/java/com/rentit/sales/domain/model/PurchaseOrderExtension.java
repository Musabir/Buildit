package com.rentit.sales.domain.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.time.LocalDate;


@Entity
@Getter
@NoArgsConstructor(force=true,access= AccessLevel.PROTECTED)
@AllArgsConstructor(staticName="of")
public class PurchaseOrderExtension {
    @Id
    String id;

    LocalDate endDate;

    @Enumerated(EnumType.STRING)
    PurchaseOrderStatus status;

    public void acceptExtension(){
        status = PurchaseOrderStatus.OPEN;
    }

    public void rejectExtension(){
        status = PurchaseOrderStatus.REJECTED;
    }

}
