package com.grocerystore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record VegetableDto(
        Long id,

        String name,

        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be positive")
        Double pricePer100Grams) {
}

