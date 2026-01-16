package com.grocerystore.controller;

import com.grocerystore.dto.VegetableDto;
import com.grocerystore.service.VegetableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vegetables")
@RequiredArgsConstructor
public class VegetableController {
    private final VegetableService vegetableService;

    @PostMapping
    public ResponseEntity<VegetableDto> addVegetable(@RequestBody @Valid VegetableDto vegetableDto) {
        VegetableDto created = vegetableService.addVegetable(vegetableDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VegetableDto> updateVegetable(@PathVariable Long id, @RequestBody @Valid VegetableDto vegetableDto) {
        VegetableDto updated = vegetableService.updateVegetable(id, vegetableDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<VegetableDto>> getAllVegetables() {
        return ResponseEntity.ok(vegetableService.getAllVegetables());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VegetableDto> getVegetable(@PathVariable Long id) {
        return ResponseEntity.ok(vegetableService.getVegetable(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVegetable(@PathVariable Long id) {
        vegetableService.deleteVegetable(id);
        return ResponseEntity.noContent().build();
    }

}
