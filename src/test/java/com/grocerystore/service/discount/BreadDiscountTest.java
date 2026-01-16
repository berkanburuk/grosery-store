package com.grocerystore.service.discount;

import com.grocerystore.enums.Country;
import com.grocerystore.dto.OrderItemDto;
import com.grocerystore.model.Beer;
import com.grocerystore.model.Bread;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BreadDiscountTest {

    private BreadDiscount breadDiscount;
    private Bread bread1;
    private Bread bread2;

    @BeforeEach
    void setUp() {
        breadDiscount = new BreadDiscount();

        bread1 = new Bread();
        bread1.setId(1L);
        bread1.setName("White Bread");
        bread1.setPrice(5.0);

        bread2 = new Bread();
        bread2.setId(2L);
        bread2.setName("Whole Wheat");
        bread2.setPrice(7.0);
    }

    @Test
    void testNoBreadInOrder_shouldReturnZeroDiscount() {
        List<OrderItemDto> orderItems = new ArrayList<>();

        double discount = breadDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testBreadAge1Day_shouldReturnZeroDiscount() {
        bread1.setBakingDate(LocalDate.now().minusDays(1));

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread1, 4));
        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testBreadToday_shouldReturnZeroDiscount() {
        bread1.setBakingDate(LocalDate.now());

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread1, 4));
        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testBreadAge2Days_buy2Get1Free() {
        bread1.setBakingDate(LocalDate.now().minusDays(2));

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread1, 4));// 4 breads = 2 free

        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        // 4 breads: 4/2 = 2 free items
        // Discount: 2 * 5.0 = 10.0
        assertEquals(10.0, discount, 0.001);
    }

    @Test
    void testBreadAge3Days_buy2Get1Free() {
        bread1.setBakingDate(LocalDate.now().minusDays(3));

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread1, 5));

        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        // 5 breads: 5/2 = 2 free items
        // Discount: 2 * 5.0 = 10.0
        assertEquals(10.0, discount, 0.001);
    }

    @Test
    void testBreadAge4Days_buy3Get2Free() {
        bread1.setBakingDate(LocalDate.now().minusDays(4));

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread1, 6));
        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        // 6 breads: (6/3) * 2 = 4 free items
        // Discount: 4 * 5.0 = 20.0
        assertEquals(20.0, discount, 0.001);
    }

    @Test
    void testBreadAge5Days_buy3Get2Free() {
        bread1.setBakingDate(LocalDate.now().minusDays(6));

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread1, 7));
        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        // 7 breads: (7/3) * 2 = 4 free items
        // Discount: 4 * 5.0 = 20.0
        assertEquals(20.0, discount, 0.001);
    }

    @Test
    void testMultipleBreadsSameAge() { //******
        bread1.setBakingDate(LocalDate.now().minusDays(3));
        bread1.setPrice(5.0);

        bread2.setBakingDate(LocalDate.now().minusDays(3));
        bread2.setPrice(7.0);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(bread1, 4), // 4 breads = 2 free * 5.0 = 10.0
                new OrderItemDto(bread2, 6)  // 6 breads = 3 free * 7.0 = 21.0
        );

        // When
        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        // Then
        // Total discount: 10.0 + 21.0 = 31.0
        assertEquals(31.0, discount, 0.001);
    }

    @Test
    void testMultipleBreadsDifferentAges() {
        bread1.setBakingDate(LocalDate.now().minusDays(2));
        bread1.setPrice(5.0);

        bread2.setBakingDate(LocalDate.now().minusDays(5));
        bread2.setPrice(7.0);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(bread1, 4), // 4 breads = 2 free * 5.0 = 10.0
                new OrderItemDto(bread2, 6)  // 6 breads = 4 free * 7.0 = 28.0
        );

        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        assertEquals(38.0, discount, 0.001);
    }

    @Test
    void testMixedOrderWithBreadAndBeer() {
        // Given: Mixed order with beer and bread
        Beer beer = new Beer();
        beer.setId(1L);
        beer.setName("Duvel");
        beer.setCountry(Country.BELGIUM);
        beer.setPrice(5.0);

        bread1.setBakingDate(LocalDate.now().minusDays(3));
        bread1.setPrice(5.0);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(beer, 6),    // Beer (ignored)
                new OrderItemDto(bread1, 4)   // 4 breads = 2 free * 5.0 = 10.0
        );

        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        assertEquals(10.0, discount, 0.001);
    }

    @Test
    void testBreadAge1Day_withMultipleQuantity() {
        bread1.setBakingDate(LocalDate.now().minusDays(1));

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread1, 10));
        double discount = breadDiscount.calculateTotalDiscount(orderItems);

        assertEquals(0.0, discount, 0.001);
    }

}
