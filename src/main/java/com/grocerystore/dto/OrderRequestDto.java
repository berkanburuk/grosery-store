package com.grocerystore.dto;

import com.grocerystore.enums.ItemType;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull ItemType type,
        @NotNull Long itemId,
        @NotNull int amount
) {
}
