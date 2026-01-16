package com.grocerystore.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record BreadDto(

        Long id,

        String name,

        @NotNull(message = "Baking Date cannot be null")
        LocalDate bakingDate,

        @NotNull(message = "Price cannot be null")
        @Positive(message = "Price must be positive")
        Double price) {
}
