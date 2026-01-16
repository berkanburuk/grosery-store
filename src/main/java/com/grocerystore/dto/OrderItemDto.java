package com.grocerystore.dto;

import com.grocerystore.model.Beer;
import com.grocerystore.model.Bread;
import com.grocerystore.model.Item;
import com.grocerystore.model.Vegetable;
import com.grocerystore.service.discount.DiscountStrategy;
import com.grocerystore.util.PriceCalculator;
import com.grocerystore.service.discount.VegetableDiscount;

import java.util.List;
import java.util.Objects;

public record OrderItemDto(
        Item item,
        int amount
) {
    public String buildDescription() {
        return switch (item) {
            case Bread bread -> amount + " x " + bread.getName();
            case Beer beer -> amount + " x " + beer.getName() + " (" + beer.getCountry() + ")";
            case Vegetable veg -> amount + "g " + veg.getName();
            default -> throw new IllegalStateException();
        };
    }

    /**
     * Calculate net for this line using strategies.
     * Vegetable discount is handled separately in its strategy using allItems.
     */
    public double calculateNet(List<OrderItemDto> allItems, List<DiscountStrategy> discounts) {
        double gross = PriceCalculator.calculateGrossTotal(List.of(this));

        // Vegetable discount uses total weight of vegetables
        DiscountStrategy vegStrategy = discounts.stream()
                .filter(ds -> ds instanceof VegetableDiscount)
                .findFirst()
                .orElse(null);

        if (Objects.nonNull(vegStrategy) && item instanceof Vegetable) {
            double totalVegGross = allItems.stream()
                    .filter(i -> i.item() instanceof Vegetable)
                    .mapToDouble(i -> i.item().basePrice() * i.amount() / 100.0)
                    .sum();

            double vegDiscount = vegStrategy.calculateTotalDiscount(allItems);
            double proportion = gross / totalVegGross;

            return gross - vegDiscount * proportion;
        }

        // For other items, apply line-level discounts
        double discount = discounts.stream()
                .filter(ds -> !(ds instanceof VegetableDiscount))
                .mapToDouble(ds -> ds.discountPerItem(this))
                .sum();

        return gross - discount;
    }

}