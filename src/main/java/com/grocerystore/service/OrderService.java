package com.grocerystore.service;

import com.grocerystore.dto.OrderLineSummaryDto;
import com.grocerystore.dto.OrderRequestDto;
import com.grocerystore.dto.OrderSummaryDto;
import com.grocerystore.dto.OrderItemDto;
import com.grocerystore.model.Bread;
import com.grocerystore.model.Item;
import com.grocerystore.service.discount.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final List<DiscountStrategy> discountStrategies;
    private final BeerService beerService;
    private final BreadService breadService;
    private final VegetableService vegetableService;


    private List<OrderItemDto> resolveItems(List<OrderRequestDto> items) {
        return items.stream()
                .map(dto -> new OrderItemDto(resolveItem(dto), dto.amount()))
                .toList();
    }

    private Item resolveItem(OrderRequestDto dto) {
        return switch (dto.type()) {
            case BEER -> beerService.getById(dto.itemId());
            case BREAD -> breadService.getById(dto.itemId());
            case VEGETABLE -> vegetableService.getById(dto.itemId());
        };
    }

    private void validateBread(List<OrderItemDto> items) {
        for (OrderItemDto dto : items) {
            if (dto.item() instanceof Bread bread) {
                long age = ChronoUnit.DAYS.between(bread.getBakingDate(), LocalDate.now());
                if (age > 6) throw new IllegalArgumentException("Bread too old: " + bread.getName());
            }
        }
    }

    public OrderSummaryDto processOrder(List<OrderRequestDto> items) {
        List<OrderItemDto> resolvedItems = resolveItems(items);

        validateBread(resolvedItems);

        List<OrderLineSummaryDto> allLines = generateOrderReceipt(resolvedItems);

        double total = allLines.
                stream()
                .mapToDouble(OrderLineSummaryDto::total).
                sum();

        total = round(total);

        return new OrderSummaryDto(allLines, total);
    }

    private List<OrderLineSummaryDto> generateOrderReceipt(List<OrderItemDto> items) {
        return items.stream()
                .map(line -> new OrderLineSummaryDto(
                        line.buildDescription(),
                        line.calculateNet(items, discountStrategies)))
                .toList();
    }


    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }


}