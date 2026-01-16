package com.grocerystore.service;

import com.grocerystore.dto.BeerDto;
import com.grocerystore.enums.Country;
import com.grocerystore.exception.AlreadyExistsException;
import com.grocerystore.exception.NotFoundException;
import com.grocerystore.model.Beer;
import com.grocerystore.repository.BeerRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BeerService {

    private final BeerRepository beerRepository;

    @Transactional
    public BeerDto addBeer(@Valid BeerDto beerDto) {
        Beer beerExists = beerRepository.findByCountry(beerDto.country());
        if (Objects.nonNull(beerExists)) {
            throw new AlreadyExistsException("Beer already exists.");
        }

        Beer beer = new Beer();
        beer.setName(beerDto.name());
        beer.setCountry(Country.valueOf(beerDto.country().toString().toUpperCase()));
        beer.setPrice(beerDto.price());
        Beer saved = beerRepository.save(beer);
        return new BeerDto(
                beer.getId(),
                saved.getName(),
                saved.getCountry(),
                saved.getPrice()
        );
    }

    @Transactional
    public BeerDto updateBeer(Long id, @Valid BeerDto beerDto) {
        Beer beer = beerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Beer not found with id: " + id));

        beer.setName(beerDto.name());
        beer.setCountry(Country.valueOf(beerDto.country().toString().toUpperCase()));
        beer.setPrice(beerDto.price());

        Beer updated = beerRepository.save(beer);
        return new BeerDto(
                updated.getId(),
                updated.getName(),
                updated.getCountry(),
                updated.getPrice());
    }

    public List<BeerDto> getAllBeers() {
        List<Beer> beerList = beerRepository.findAll();
        return beerList.stream().
                map(beer ->
                        new BeerDto(
                                beer.getId(),
                                beer.getName(),
                                beer.getCountry(),
                                beer.getPrice()
                        ))
                .toList();
    }

    @Transactional
    public void deleteBeer(Long id) {
        Beer beer = beerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Beer not found with id: " + id));
        beerRepository.delete(beer);
    }

    public BeerDto getBeer(Long id) {
        Beer beer = beerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Beer not found with id: " + id));
        return new BeerDto(
                beer.getId(),
                beer.getName(),
                beer.getCountry(),
                beer.getPrice());
    }

    public Beer getById(Long id) {
        return beerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Beer not found: " + id));
    }

}
