package com.grocerystore.util;


import com.grocerystore.dto.OrderItemDto;
import com.grocerystore.model.Beer;
import com.grocerystore.model.Bread;
import com.grocerystore.model.Item;
import com.grocerystore.model.Vegetable;

import java.util.List;

public final class PriceCalculator {

    private PriceCalculator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static double calculateGrossTotal(List<OrderItemDto> items) {
        return items.stream()
                .mapToDouble(line -> calculateTotal(line.item(), line.amount()))
                .sum();
    }

    private static double calculateTotal(Item item, int amount) {
        if (item instanceof Beer beer) {
            return beer.basePrice() * amount;
        }
        if (item instanceof Bread bread) {
            return bread.basePrice() * amount;
        }
        if (item instanceof Vegetable veg) {
            return veg.getPricePer100Grams() * (amount / 100.0);
        }
        throw new IllegalArgumentException("Unknown item type");
    }
}