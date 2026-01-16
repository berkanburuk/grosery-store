package com.grocerystore.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "bread")
@Getter
@Setter
public class Bread implements Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private LocalDate bakingDate;


    @Override
    public Double basePrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }


}
