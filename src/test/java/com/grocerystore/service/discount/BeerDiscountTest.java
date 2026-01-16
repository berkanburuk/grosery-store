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

class BeerDiscountTest {

    private BeerDiscount beerDiscount;
    private Beer belgianBeer;
    private Beer dutchBeer;
    private Beer germanBeer;

    @BeforeEach
    void setUp() {
        beerDiscount = new BeerDiscount();

        belgianBeer = new Beer();
        belgianBeer.setId(1L);
        belgianBeer.setName("Duvel");
        belgianBeer.setCountry(Country.BELGIUM);
        belgianBeer.setPrice(5.0);

        dutchBeer = new Beer();
        dutchBeer.setId(2L);
        dutchBeer.setName("Heineken");
        dutchBeer.setCountry(Country.NETHERLANDS);
        dutchBeer.setPrice(4.0);

        germanBeer = new Beer();
        germanBeer.setId(3L);
        germanBeer.setName("Beck's");
        germanBeer.setCountry(Country.GERMANY);
        germanBeer.setPrice(3.5);
    }

    @Test
    void testNoBeerInOrder_shouldReturnZeroDiscount() {
        List<OrderItemDto> orderItems = new ArrayList<>();

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testOnlyBreadInOrder_shouldReturnZeroDiscount() {
        Bread bread = new Bread();
        bread.setId(1L);
        bread.setName("White Bread");
        bread.setPrice(5.0);
        bread.setBakingDate(LocalDate.now().minusDays(3));

        List<OrderItemDto> orderItems = List.of(new OrderItemDto(bread, 4));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testBelgianBeer_lessThan6_shouldReturnZeroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(belgianBeer, 5));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.0, discount, 0.001);
    }

    @Test
    void testBelgianBeer_exactly6_shouldGet3EuroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(belgianBeer, 6));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        // 1 pack * 3.0 EUR = 3.0
        assertEquals(3.0, discount, 0.001);
    }

    @Test
    void testBelgianBeer_12beers_shouldGet6EuroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(belgianBeer, 12));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(6.0, discount, 0.001);
    }

    @Test
    void testBelgianBeer_13beers_shouldStillGet6EuroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(belgianBeer, 13));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(6.0, discount, 0.001);
    }

    @Test
    void testDutchBeer_exactly6_shouldGet2EuroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(dutchBeer, 6));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(2.0, discount, 0.001);
    }

    @Test
    void testDutchBeer_18beers_shouldGet6EuroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(dutchBeer, 18));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(6.0, discount, 0.001);
    }

    @Test
    void testGermanBeer_exactly6_shouldGet4EuroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(germanBeer, 6));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(4.0, discount, 0.001);
    }

    @Test
    void testGermanBeer_24beers_shouldGet16EuroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(germanBeer, 24));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(16.0, discount, 0.001);
    }

    @Test
    void testMixedBeerCountries_shouldDiscountPerItemDiscountsPerCountry() {
        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(belgianBeer, 6), // 1 pack * 3.0 = 3.0
                new OrderItemDto(dutchBeer, 6),   // 1 pack * 2.0 = 2.0
                new OrderItemDto(germanBeer, 6)   // 1 pack * 4.0 = 4.0
        );

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(9.0, discount, 0.001);
    }

    @Test
    void testMultipleBelgianBeers_shouldCombineQuantities() {
        Beer belgianBeer2 = new Beer();
        belgianBeer2.setId(4L);
        belgianBeer2.setName("Westmalle");
        belgianBeer2.setCountry(Country.BELGIUM);
        belgianBeer2.setPrice(6.0);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(belgianBeer, 4),  // 4 Belgian
                new OrderItemDto(belgianBeer2, 8)  // 8 Belgian
        );

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        // Total Belgian beers: 4 + 8 = 12 (2 packs)
        assertEquals(6.0, discount, 0.001);
    }

    @Test
    void testMultipleDutchBeers_shouldCombineQuantities() {
        Beer dutchBeer2 = new Beer();
        dutchBeer2.setId(5L);
        dutchBeer2.setName("Grolsch");
        dutchBeer2.setCountry(Country.NETHERLANDS);
        dutchBeer2.setPrice(3.8);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(dutchBeer, 5),   // 5 Dutch
                new OrderItemDto(dutchBeer2, 7)   // 7 Dutch
        );

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        // Total Dutch beers: 5 + 7 = 12 (2 packs)
        assertEquals(4.0, discount, 0.001);
    }

    @Test
    void testMixedOrderWithBeerAndBread() {
        Bread bread = new Bread();
        bread.setId(1L);
        bread.setName("White Bread");
        bread.setPrice(5.0);
        bread.setBakingDate(LocalDate.now().minusDays(3));

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(bread, 4),         // Bread (ignored)
                new OrderItemDto(belgianBeer, 12)
        );

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        // 12 Belgian beers = 2 packs * 3.0 = 6.0
        assertEquals(6.0, discount, 0.001);
    }

    @Test
    void testComplexMixedBeerOrder() {
        Beer belgianBeer2 = new Beer();
        belgianBeer2.setId(4L);
        belgianBeer2.setName("Westmalle");
        belgianBeer2.setCountry(Country.BELGIUM);
        belgianBeer2.setPrice(6.0);

        Beer germanBeer2 = new Beer();
        germanBeer2.setId(5L);
        germanBeer2.setName("Warsteiner");
        germanBeer2.setCountry(Country.GERMANY);
        germanBeer2.setPrice(3.8);

        List<OrderItemDto> orderItems = List.of(
                new OrderItemDto(belgianBeer, 7),   // 7 Belgian
                new OrderItemDto(belgianBeer2, 5),  // 5 Belgian -> Total: 12 (2 packs)
                new OrderItemDto(dutchBeer, 8),     // 8 Dutch (1 pack)
                new OrderItemDto(germanBeer, 10),   // 10 German
                new OrderItemDto(germanBeer2, 8)    // 8 German -> Total: 18 (3 packs)
        );

        double discount = beerDiscount.calculateTotalDiscount(orderItems);

        // Then
        // Belgian: 12 total = 2 packs * 3.0 = 6.0
        // Dutch: 8 total = 1 pack * 2.0 = 2.0
        // German: 18 total = 3 packs * 4.0 = 12.0
        assertEquals(20.0, discount, 0.001);
    }

    @Test
    void testSingleBeer_shouldReturnZeroDiscount() {
        List<OrderItemDto> orderItems = List.of(new OrderItemDto(belgianBeer, 1));

        double discount = beerDiscount.calculateTotalDiscount(orderItems);
        assertEquals(0.0, discount, 0.001);
    }
}
