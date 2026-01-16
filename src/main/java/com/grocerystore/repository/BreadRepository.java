package com.grocerystore.repository;

import com.grocerystore.model.Bread;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BreadRepository extends JpaRepository<Bread, Long> {
}
