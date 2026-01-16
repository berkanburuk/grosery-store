package com.grocerystore.service;

import com.grocerystore.dto.OrderRequestDto;
import com.grocerystore.dto.OrderSummaryDto;
import com.grocerystore.enums.Country;
import com.grocerystore.enums.ItemType;
import com.grocerystore.model.Beer;
import com.grocerystore.model.Bread;
import com.grocerystore.model.Vegetable;
import com.grocerystore.service.discount.BeerDiscount;
import com.grocerystore.service.discount.BreadDiscount;
import com.grocerystore.service.discount.DiscountStrategy;
import com.grocerystore.service.discount.VegetableDiscount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private BeerService beerService;

    @Mock
    private BreadService breadService;

    @Mock
    private VegetableService vegetableService;

    @InjectMocks
    private OrderService orderService;

    private List<DiscountStrategy> discountStrategies;

    private Beer beer;
    private Bread bread;
    private Vegetable vegetable;

    @BeforeEach
    void setUp() {
        // Create discount strategies
        discountStrategies = List.of(
                new BeerDiscount(),
                new BreadDiscount(),
                new VegetableDiscount()
        );

        // Inject strategies
        orderService = new OrderService(
                discountStrategies,
                beerService,
                breadService,
                vegetableService
        );

        // Create test items
        beer = new Beer();
        beer.setId(1L);
        beer.setName("Duvel");
        beer.setCountry(Country.BELGIUM);
        beer.setPrice(5.0);

        bread = new Bread();
        bread.setId(1L);
        bread.setName("White Bread");
        bread.setPrice(5.0);
        bread.setBakingDate(LocalDate.now().minusDays(3));

        vegetable = new Vegetable();
        vegetable.setId(1L);
        vegetable.setName("Broccoli");
        vegetable.setPricePer100Grams(1.5);
    }

    @Test
    void testProcessOrder_onlyBeer_noDiscount() {
        OrderRequestDto orderRequestDto = new OrderRequestDto(ItemType.BEER, 1L, 5);
        when(beerService.getById(1L)).thenReturn(beer);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(orderRequestDto));

        // Gross: 5 * 5.0 = 25.0, Discount: 0, Net: 25.0
        assertEquals(25.0, orderSummaryDto.total(), 0.001);
    }

    @Test
    void testProcessOrder_onlyBeer_withPackDiscount() {
        OrderRequestDto orderRequestDto = new OrderRequestDto(ItemType.BEER, 1L, 6);
        when(beerService.getById(1L)).thenReturn(beer);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(orderRequestDto));

        // Gross: 6 * 5.0 = 30.0, Discount: 1 pack * 3.0 = 3.0, Net: 30.0 - 3.0 = 27.0
        assertEquals(27.0, orderSummaryDto.total(), 0.001);
    }

    @Test
    void testProcessOrder_onlyBread_withDiscount() {
        OrderRequestDto orderRequestDto = new OrderRequestDto(ItemType.BREAD, 1L, 4);
        when(breadService.getById(1L)).thenReturn(bread);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(orderRequestDto));

        // Gross: 4 * 5.0 = 20.0, Discount: 2 free bread * 5.0 = 10.0, Net: 20.0 - 10.0 = 10.0
        assertEquals(10.0, orderSummaryDto.total(), 0.001);
    }

    @Test
    void testProcessOrder_onlyVegetable_withDiscount() {
        OrderRequestDto orderRequestDto = new OrderRequestDto(ItemType.VEGETABLE, 1L, 300);
        when(vegetableService.getById(1L)).thenReturn(vegetable);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(orderRequestDto));

        // Gross: 1.5 * (300/100) = 4.5, Discount: 4.5 * 0.07 = 0.315, Net: 4.5 - 0.315 = 4.185
        assertEquals(4.18, orderSummaryDto.total());
    }

    @Test
    void testProcessOrder_mixedOrder_withAllDiscounts() {
        OrderRequestDto beerOrder = new OrderRequestDto(ItemType.BEER, 1L, 6);
        OrderRequestDto breadOrder = new OrderRequestDto(ItemType.BREAD, 1L, 4);
        OrderRequestDto vegOrder = new OrderRequestDto(ItemType.VEGETABLE, 1L, 300);

        when(beerService.getById(1L)).thenReturn(beer);
        when(breadService.getById(1L)).thenReturn(bread);
        when(vegetableService.getById(1L)).thenReturn(vegetable);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(beerOrder, breadOrder, vegOrder));

        // Beer Gross: 6 * 5.0 = 30.0, Discount: 3.0 = Net: 27.0
        // Bread Gross: 4 * 5.0 = 20.0, Discount: 10.0 = Net: 10.0
        // Vegetable Gross: 4.5, Discount: 0.315 = Net: 4.185
        // Total: 27.0 + 10.0 + 4.185 = ~41.185
        assertEquals(41.19, orderSummaryDto.total());
    }

    @Test
    void testProcessOrder_multipleBeerOrders_Discount() {
        Beer beer2 = new Beer();
        beer2.setId(2L);
        beer2.setName("Heineken");
        beer2.setCountry(Country.NETHERLANDS);
        beer2.setPrice(6.0);

        OrderRequestDto beerOrder1 = new OrderRequestDto(ItemType.BEER, 1L, 4);
        OrderRequestDto beerOrder2 = new OrderRequestDto(ItemType.BEER, 2L, 8);

        when(beerService.getById(1L)).thenReturn(beer);
        when(beerService.getById(2L)).thenReturn(beer2);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(beerOrder1, beerOrder2));

        // Beer1 Gross: 4 * 5.0 = 20.0 + Beer2 Gross: 8 * 6.0 = 48.0
        // Total Gross: 68.0
        // Belgian beers: 4 + 8 = 12 = 2 packs * 3.0 = 6.0 discount
        assertEquals(66.0, orderSummaryDto.total(), 0.001);
    }

    @Test
    void testProcessOrder_breadTooOld_shouldThrowException() {
        Bread oldBread = new Bread();
        oldBread.setId(2L);
        oldBread.setName("Old Bread");
        oldBread.setPrice(5.0);
        oldBread.setBakingDate(LocalDate.now().minusDays(7));

        OrderRequestDto orderRequestDto = new OrderRequestDto(ItemType.BREAD, 2L, 2);
        when(breadService.getById(2L)).thenReturn(oldBread);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> orderService.processOrder(List.of(orderRequestDto))
        );

        assertTrue(exception.getMessage().contains("Bread too old"));
    }

    @Test
    void testProcessOrder_breadExactly6DaysOld_shouldProcess() {
        Bread sixDayBread = new Bread();
        sixDayBread.setId(2L);
        sixDayBread.setName("Six Day Bread");
        sixDayBread.setPrice(5.0);
        sixDayBread.setBakingDate(LocalDate.now().minusDays(6));

        OrderRequestDto orderRequestDto = new OrderRequestDto(ItemType.BREAD, 2L, 3);
        when(breadService.getById(2L)).thenReturn(sixDayBread);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(orderRequestDto));

        // Gross: 3 * 5.0 = 15.0
        // Discount (6 days old, buy 3 get 2 free): 2 * 5.0 = 10.0
        // Net: 15.0 - 10.0 = 5.0
        assertEquals(5.0, orderSummaryDto.total(), 0.001);
    }

    @Test
    void testProcessOrder_emptyOrder_shouldReturnZero() {
        List<OrderRequestDto> emptyOrder = List.of();

        OrderSummaryDto orderSummaryDto = orderService.processOrder(emptyOrder);

        assertEquals(0.0, orderSummaryDto.total());
    }

    @Test
    void testProcessOrder_complexMixedOrder() {
        Beer dutchBeer = new Beer();
        dutchBeer.setId(2L);
        dutchBeer.setName("Heineken");
        dutchBeer.setCountry(Country.NETHERLANDS);
        dutchBeer.setPrice(4.0);

        Bread oldBread = new Bread();
        oldBread.setId(2L);
        oldBread.setName("Old Bread");
        oldBread.setPrice(7.0);
        oldBread.setBakingDate(LocalDate.now().minusDays(5));

        Vegetable carrot = new Vegetable();
        carrot.setId(2L);
        carrot.setName("Carrot");
        carrot.setPricePer100Grams(1.0);

        OrderRequestDto beerOrder1 = new OrderRequestDto(ItemType.BEER, 1L, 8); // Belgian
        OrderRequestDto beerOrder2 = new OrderRequestDto(ItemType.BEER, 2L, 6); // Dutch
        OrderRequestDto breadOrder1 = new OrderRequestDto(ItemType.BREAD, 1L, 4); // 3 days old
        OrderRequestDto breadOrder2 = new OrderRequestDto(ItemType.BREAD, 2L, 6); // 5 days old
        OrderRequestDto vegOrder1 = new OrderRequestDto(ItemType.VEGETABLE, 1L, 300); // Broccoli
        OrderRequestDto vegOrder2 = new OrderRequestDto(ItemType.VEGETABLE, 2L, 250); // Carrot

        when(beerService.getById(1L)).thenReturn(beer);
        when(beerService.getById(2L)).thenReturn(dutchBeer);
        when(breadService.getById(1L)).thenReturn(bread);
        when(breadService.getById(2L)).thenReturn(oldBread);
        when(vegetableService.getById(1L)).thenReturn(vegetable);
        when(vegetableService.getById(2L)).thenReturn(carrot);

        OrderSummaryDto orderSummaryDto = orderService.processOrder(List.of(beerOrder1, beerOrder2, breadOrder1,
                breadOrder2, vegOrder1, vegOrder2));

        // Then
        // Beer calculations:
        // Belgian: 8 * 5.0 = 40.0, discount: 1 pack * 3.0 = 3.0
        // Dutch: 6 * 4.0 = 24.0, discount: 1 pack * 2.0 = 2.0
        // Beer subtotal: 40.0 + 24.0 - 3.0 - 2.0 = 59.0

        // Bread calculations:
        // 3-day bread: 4 * 5.0 = 20.0, discount: 2 * 5.0 = 10.0
        // 5-day bread: 6 * 7.0 = 42.0, discount: 4 * 7.0 = 28.0
        // Bread subtotal: 20.0 + 42.0 - 10.0 - 28.0 = 24.0

        // Vegetable calculations:
        // Total weight: 300 + 250 = 550g (>500, so 10% discount)
        // Broccoli: 1.5 * 3 = 4.5
        // Carrot: 1.0 * 2.5 = 2.5
        // Total veg: 7.0, discount: 7.0 * 0.10 = 0.7
        // Veg subtotal: 7.0 - 0.7 = 6.3

        // Grand total: 59.0 + 24.0 + 6.3 = 89.3
        assertEquals(89.3, orderSummaryDto.total(), 0.001);
    }

}
