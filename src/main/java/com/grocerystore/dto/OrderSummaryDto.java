package com.grocerystore.dto;

import java.util.List;

public record OrderSummaryDto(
        List<OrderLineSummaryDto> lines,
        double total
) {
}

