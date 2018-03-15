package com.rentit.inventory.domain.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(force=true,access= AccessLevel.PRIVATE)
@AllArgsConstructor(staticName="of")
public class PlantInventoryItem {
    @Id
    String id;
    String serialNumber;

    @Enumerated(EnumType.STRING)
    EquipmentCondition equipmentCondition;

    @Enumerated(EnumType.STRING)
    ItemStatus itemStatus;

    @ManyToOne(cascade = CascadeType.PERSIST)
    PlantInventoryEntry plantInfo;
}
