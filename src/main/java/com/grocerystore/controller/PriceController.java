package com.grocerystore.controller;

import com.grocerystore.service.BeerService;
import com.grocerystore.service.BreadService;
import com.grocerystore.service.VegetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/prices")
@RequiredArgsConstructor
public class PriceController {

    private final BeerService beerService;
    private final BreadService breadService;
    private final VegetableService vegetableService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPrices() {
        return ResponseEntity.ok(Map.of(
                "beers", beerService.getAllBeers(),
                "breads", breadService.getAllBreads(),
                "vegetables", vegetableService.getAllVegetables()
        ));
    }
}
