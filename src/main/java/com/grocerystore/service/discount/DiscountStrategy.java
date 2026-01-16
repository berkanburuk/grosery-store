package com.grocerystore.service.discount;

import com.grocerystore.dto.OrderItemDto;

import java.util.List;

public interface DiscountStrategy {
    double calculateTotalDiscount(List<OrderItemDto> itemList);

    double discountPerItem(OrderItemDto line);
}
