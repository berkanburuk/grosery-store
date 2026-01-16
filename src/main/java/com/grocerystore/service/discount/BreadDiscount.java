package com.grocerystore.service.discount;

import com.grocerystore.dto.OrderItemDto;
import com.grocerystore.model.Bread;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class BreadDiscount implements DiscountStrategy {

    @Override
    public double calculateTotalDiscount(List<OrderItemDto> orderItems) {
        return orderItems.stream()
                .mapToDouble(this::calculateDiscountForLine)
                .sum();
    }

    @Override
    public double discountPerItem(OrderItemDto line) {
        return calculateDiscountForLine(line);
    }

    private double calculateDiscountForLine(OrderItemDto line) {
        if (!(line.item() instanceof Bread bread)) return 0;

        long age = ChronoUnit.DAYS.between(bread.getBakingDate(), LocalDate.now());

        if (age <= 1 || age > 6) return 0;

        int free = (age <= 3)
                ? line.amount() / 2
                : (line.amount() / 3) * 2;

        return free * bread.basePrice();
    }
}
