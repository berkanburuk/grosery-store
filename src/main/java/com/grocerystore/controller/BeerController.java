package com.grocerystore.controller;


import com.grocerystore.dto.BeerDto;
import com.grocerystore.service.BeerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beers")
@RequiredArgsConstructor
public class BeerController {

    private final BeerService beerService;

    @PostMapping
    public ResponseEntity<BeerDto> addBeer(@RequestBody @Valid BeerDto beerDto) {
        BeerDto created = beerService.addBeer(beerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeerDto> updateBeer(@PathVariable Long id, @RequestBody @Valid BeerDto beerDto) {
        BeerDto updated = beerService.updateBeer(id, beerDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<BeerDto>> getAllBeers() {
        return ResponseEntity.ok(beerService.getAllBeers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerDto> getBeer(@PathVariable Long id) {
        return ResponseEntity.ok(beerService.getBeer(id));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeer(@PathVariable Long id) {
        beerService.deleteBeer(id);
        return ResponseEntity.noContent().build();
    }

}
