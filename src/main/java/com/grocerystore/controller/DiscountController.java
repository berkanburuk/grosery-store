package com.grocerystore.controller;

import com.grocerystore.service.discount.DiscountStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
public class DiscountController {

    private final List<DiscountStrategy> discountStrategies;

    @GetMapping
    public ResponseEntity<List<String>> getDiscountRules() {
        List<String> rules = discountStrategies.stream()
                .map(ds -> ds.getClass().getSimpleName())
                .toList();
        return ResponseEntity.ok(rules);
    }
}