package com.grocerystore.dto;

import com.grocerystore.enums.Country;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record BeerDto(
        Long id,

        String name,

        @NotNull(message = "Country cannot be null")
        Country country,

        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be positive")
        Double price) {
}
