package com.grocerystore.service;

import com.grocerystore.dto.VegetableDto;
import com.grocerystore.exception.NotFoundException;
import com.grocerystore.model.Vegetable;
import com.grocerystore.repository.VegetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VegetableService {

    private final VegetableRepository vegetableRepository;

    @Transactional
    public VegetableDto addVegetable(VegetableDto vegetableDto) {

        Vegetable vegetable = new Vegetable();
        if (vegetableDto.name() != null && !vegetableDto.name().trim().isEmpty()) {
            vegetable.setName(vegetableDto.name());
        }

        vegetable.setPricePer100Grams(vegetableDto.pricePer100Grams());
        Vegetable saved = vegetableRepository.save(vegetable);

        return new VegetableDto(
                saved.getId(),
                saved.getName(),
                saved.getPricePer100Grams());
    }

    @Transactional
    public VegetableDto updateVegetable(Long id, VegetableDto vegetableDto) {
        Vegetable vegetable = vegetableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vegetable not found with id: " + id));
        vegetable.setName(vegetableDto.name());
        vegetable.setPricePer100Grams(vegetableDto.pricePer100Grams());
        Vegetable updated = vegetableRepository.save(vegetable);

        return new VegetableDto(
                updated.getId(),
                updated.getName(),
                updated.getPricePer100Grams());

    }

    public List<VegetableDto> getAllVegetables() {
        return vegetableRepository.findAll()
                .stream()
                .map(vegetable ->
                        new VegetableDto(
                                vegetable.getId(),
                                vegetable.getName(),
                                vegetable.getPricePer100Grams()
                        ))
                .toList();
    }

    @Transactional
    public void deleteVegetable(Long id) {
        Vegetable vegetable = vegetableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vegetable not found with id: " + id));
        vegetableRepository.delete(vegetable);
    }

    public VegetableDto getVegetable(Long id) {
        Vegetable vegetable = vegetableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vegetable not found with id: " + id));
        return new VegetableDto(
                vegetable.getId(),
                vegetable.getName(),
                vegetable.getPricePer100Grams());
    }

    public Vegetable getById(Long id) {
        return vegetableRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Vegetable not found: " + id));
    }

}
