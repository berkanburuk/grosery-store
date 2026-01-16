package com.grocerystore.service;

import com.grocerystore.dto.BreadDto;
import com.grocerystore.exception.NotFoundException;
import com.grocerystore.model.Bread;
import com.grocerystore.repository.BreadRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BreadService {

    private final BreadRepository breadRepository;

    @Transactional
    public BreadDto addBread(BreadDto breadDto) {

        if (breadDto.bakingDate().isBefore(java.time.LocalDate.now().minusDays(6))) {
            throw new IllegalArgumentException("Breads older than 6 days cannot be handled.");
        }

        Bread bread = new Bread();
        bread.setName(breadDto.name());
        bread.setBakingDate(breadDto.bakingDate());
        bread.setPrice(breadDto.price());
        Bread saved = breadRepository.save(bread);
        return new BreadDto(
                saved.getId(),
                saved.getName(),
                saved.getBakingDate(),
                saved.getPrice()
        );
    }

    @Transactional
    public BreadDto updateBread(Long id, BreadDto breadDto) {
        if (breadDto.bakingDate().isBefore(java.time.LocalDate.now().minusDays(6))) {
            throw new IllegalArgumentException("Breads older than 6 days cannot be handled.");
        }

        Bread bread = breadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bread not found with id: " + id));

        bread.setName(breadDto.name());
        bread.setBakingDate(breadDto.bakingDate());
        bread.setPrice(breadDto.price());

        Bread updated = breadRepository.save(bread);
        return new BreadDto(
                updated.getId(),
                updated.getName(),
                updated.getBakingDate(),
                updated.getPrice()
        );
    }


    public List<BreadDto> getAllBreads() {
        List<Bread> breadList = breadRepository.findAll();
        return breadList.stream()
                .map(bread ->
                        new BreadDto(
                                bread.getId(),
                                bread.getName(),
                                bread.getBakingDate(),
                                bread.getPrice()
                        ))
                .toList();
    }

    @Transactional
    public void deleteBread(Long id) {
        Bread bread = breadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bread not found with id: " + id));
        breadRepository.delete(bread);
    }

    public BreadDto getBread(Long id) {
        Bread bread = breadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bread not found with id: " + id));
        return new BreadDto(
                bread.getId(),
                bread.getName(),
                bread.getBakingDate(),
                bread.getPrice()
        );
    }

    public Bread getById(Long id) {
        return breadRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bread not found: " + id));
    }

}
