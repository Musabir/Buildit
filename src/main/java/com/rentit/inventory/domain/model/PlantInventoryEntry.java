package com.rentit.inventory.domain.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(force=true,access= AccessLevel.PROTECTED)
@AllArgsConstructor(staticName="of")
public class PlantInventoryEntry {
    @Id
    String id;
    String name;
    String description;
    BigDecimal price;

}
