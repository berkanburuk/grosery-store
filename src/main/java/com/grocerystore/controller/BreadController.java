package com.grocerystore.controller;

import com.grocerystore.dto.BreadDto;
import com.grocerystore.service.BreadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/breads")
@RequiredArgsConstructor
public class BreadController {

    private final BreadService breadService;

    @PostMapping
    public ResponseEntity<BreadDto> addBread(@RequestBody @Valid BreadDto breadDto) {
        BreadDto created = breadService.addBread(breadDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BreadDto> updateBread(@PathVariable Long id, @RequestBody @Valid BreadDto breadDto) {
        BreadDto updated = breadService.updateBread(id, breadDto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping
    public ResponseEntity<List<BreadDto>> getAllBread() {
        return ResponseEntity.ok(breadService.getAllBreads());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BreadDto> getBread(@PathVariable Long id) {
        return ResponseEntity.ok(breadService.getBread(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBread(@PathVariable Long id) {
        breadService.deleteBread(id);
        return ResponseEntity.noContent().build();
    }

}
