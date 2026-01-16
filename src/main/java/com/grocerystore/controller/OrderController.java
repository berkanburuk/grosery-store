package com.grocerystore.controller;

import com.grocerystore.dto.OrderRequestDto;
import com.grocerystore.dto.OrderSummaryDto;
import com.grocerystore.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderSummaryDto> processOrder(@RequestBody @Valid List<OrderRequestDto> orderRequestDtos) {
        OrderSummaryDto created = orderService.processOrder(orderRequestDtos);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}