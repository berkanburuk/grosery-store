package com.grocerystore.dto;

public record OrderLineSummaryDto(
        String description,
        double total
) {
}