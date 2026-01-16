package com.grocerystore.service.discount;


import com.grocerystore.enums.Country;
import com.grocerystore.dto.OrderItemDto;
import com.grocerystore.model.Beer;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BeerDiscount implements DiscountStrategy {
    @Override
    public double calculateTotalDiscount(List<OrderItemDto> orderItems) {
        // Group by Country
        Map<Country, Integer> beerQuantitiesByCountry = orderItems.stream()
                .filter(order -> order.item() instanceof Beer)
                .collect(Collectors.groupingBy(
                        order -> ((Beer) order.item()).getCountry(),
                        Collectors.summingInt(OrderItemDto::amount)
                ));

        return beerQuantitiesByCountry.entrySet().stream()
                .mapToDouble(entry -> {
                    Country country = entry.getKey();
                    int totalQuantity = entry.getValue();
                    int numberOfPacks = totalQuantity / 6;

                    double packDiscount = switch (country) {
                        case BELGIUM -> 3.0;
                        case NETHERLANDS -> 2.0;
                        case GERMANY -> 4.0;
                        default -> 0.0;
                    };

                    return numberOfPacks * packDiscount;
                })
                .sum();
    }

    @Override
    public double discountPerItem(OrderItemDto line) {
        if (!(line.item() instanceof Beer beer)) return 0;

        int packs = line.amount() / 6;

        double packDiscount = switch (beer.getCountry()) {
            case BELGIUM -> 3.0;
            case NETHERLANDS -> 2.0;
            case GERMANY -> 4.0;
        };

        return packs * packDiscount;
    }
}

