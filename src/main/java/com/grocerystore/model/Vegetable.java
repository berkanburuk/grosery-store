package com.grocerystore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "vegetable")
@Getter
@Setter
public class Vegetable implements Item {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(nullable = false)
    private Double pricePer100Grams;

    @Override
    public Double basePrice() {
        return pricePer100Grams;
    }

    @Override
    public String getName() {
        return name;
    }

}
