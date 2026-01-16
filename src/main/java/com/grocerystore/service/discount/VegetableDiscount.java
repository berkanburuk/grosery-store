package com.grocerystore.service.discount;

import com.grocerystore.dto.OrderItemDto;
import com.grocerystore.model.Vegetable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VegetableDiscount implements DiscountStrategy {

    @Override
    public double calculateTotalDiscount(List<OrderItemDto> orderItems) {
        List<OrderItemDto> vegLines = orderItems.stream()
                .filter(line -> line.item() instanceof Vegetable)
                .toList();

        double totalDiscount = 0;
        for (OrderItemDto line : vegLines) {
            totalDiscount += calculateDiscountForLine(line, vegLines);
        }
        return totalDiscount;
    }

    @Override
    public double discountPerItem(OrderItemDto line) {
        if (!(line.item() instanceof Vegetable)) return 0;
        return calculateDiscountForLine(line, List.of(line));
    }

    private double calculateDiscountForLine(OrderItemDto line, List<OrderItemDto> allVegLines) {
        int totalWeight = allVegLines.stream().mapToInt(OrderItemDto::amount).sum();

        double discountRate = totalWeight == 0 ? 0
                : totalWeight <= 100 ? 0.05
                : totalWeight <= 500 ? 0.07
                : 0.10;

        Vegetable veg = (Vegetable) line.item();
        double linePrice = veg.basePrice() * line.amount() / 100.0;

        return linePrice * discountRate;
    }
}
