package com.grocerystore.service.discount;

import com.grocerystore.enums.Country;
import com.grocerystore.dto.OrderItemDto;
import com.grocerystore.model.Beer;
import com.grocerystore.model.Vegetable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VegetableDiscountTest {

    private VegetableDiscount vegetableDiscount;
    private Vegetable vegetable1;
    private Vegetable vegetable2;

    @BeforeEach
    void setUp() {
        vegetableDiscount = new VegetableDiscount();

        vegetable1 = new Vegetable();
        vegetable1.setId(1L);
        vegetable1.setName("Broccoli");
        vegetable1.setPricePer100Grams(1.5);

        vegetable2 = new Vegetable();
        vegetable2.setId(2L);
        vegetable2.setName("Carrot");
        vegetable2.setPricePer100Grams(1.0);
    }

    @Test
    void testNoVegetablesInOrder_shouldReturnZeroDiscount() {
        List<OrderItemDto> orderItems = new ArrayList<>();

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testOnlyBeerInOrder_shouldReturnZeroDiscount() {
        Beer beer = new Beer();
        beer.setId(1L);
        beer.setName("Duvel");
        beer.setCountry(Country.BELGIUM);
        beer.setPrice(5.0);

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(beer, 6));

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void test100GramsOrLess_shouldGet5PercentDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(vegetable1, 100));

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);

        assertEquals(0.075, discount, 0.001);
    }

    @Test
    void test500GramsExactly_shouldGet7PercentDiscount() {
        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(vegetable1, 400),
                new OrderItemDto(vegetable2, 100)
        );

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.4900, discount, 0.001);
    }

    @Test
    void testMoreThan500Grams_shouldGet10PercentDiscount() {
        // Given: 750 grams total (above 500)
        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(vegetable1, 500), // 500g * 1.5 = 7.5
                new OrderItemDto(vegetable2, 250)  // 250g * 1.0 = 2.5
        );

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);

        // Total price: 7.5 + 2.5 = 10.0
        // Discount: 10.0 * 0.10 = 1.0
        assertEquals(1.0, discount, 0.001);
    }

    @Test
    void test1000Grams_shouldGet10PercentDiscount() {
        // Given: 1 kilogram
        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(vegetable1, 1000) // 1000g * 1.5 = 15.0
        );

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);

        // Total price: 1.5 * (1000/100) = 15.0
        // Discount: 15.0 * 0.10 = 1.5
        assertEquals(1.5, discount, 0.001);
    }

    @Test
    void testMixedOrderWithVegetablesAndBeer_shouldOnlyDiscountPerItemDiscountToVegetables() {
        Beer beer = new Beer();
        beer.setId(1L);
        beer.setName("Duvel");
        beer.setCountry(Country.BELGIUM);
        beer.setPrice(5.0);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(beer, 6),         // Beer (ignored)
                new OrderItemDto(vegetable1, 300)  // 300g * 1.5 = 4.5
        );

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);

        // Only vegetable price: 1.5 * (300/100) = 4.5
        // Discount: 4.5 * 0.07 = 0.315 (7% because 300g is between 101-500)
        assertEquals(0.315, discount, 0.001);
    }

    @Test
    void testMultipleDifferentVegetables_shouldCombineWeightsForDiscountTier() {
        Vegetable vegetable3 = new Vegetable();
        vegetable3.setId(3L);
        vegetable3.setName("Tomato");
        vegetable3.setPricePer100Grams(2.0);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(vegetable1, 200), // 200g * 1.5 = 3.0
                new OrderItemDto(vegetable2, 150), // 150g * 1.0 = 1.5
                new OrderItemDto(vegetable3, 250)  // 250g * 2.0 = 5.0
        );

        double discount = vegetableDiscount.calculateTotalDiscount(orderItems);

        // Total weight: 200 + 150 + 250 = 600g (>500, so 10% discount)
        // Total price: 3.0 + 1.5 + 5.0 = 9.5
        // Discount: 9.5 * 0.10 = 0.95
        assertEquals(0.95, discount, 0.001);
    }
}
