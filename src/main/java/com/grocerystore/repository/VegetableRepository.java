package com.grocerystore.repository;

import com.grocerystore.model.Vegetable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VegetableRepository extends JpaRepository<Vegetable, Long> {
}
