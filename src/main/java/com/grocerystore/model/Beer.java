package com.grocerystore.model;

import com.grocerystore.enums.Country;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "beer")
@Getter
@Setter
public class Beer implements Item {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Country country;

    @Column(nullable = false)
    private Double price;

    @Override
    public Double basePrice() {
        return price;
    }

    @Override
    public String getName() {
        return name;
    }


}