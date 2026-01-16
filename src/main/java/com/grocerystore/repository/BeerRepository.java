package com.grocerystore.repository;

import com.grocerystore.enums.Country;
import com.grocerystore.model.Beer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BeerRepository extends JpaRepository<Beer, Long> {
    Beer findByCountry(Country country);

}
