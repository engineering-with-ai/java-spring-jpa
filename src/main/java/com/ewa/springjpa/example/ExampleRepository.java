package com.ewa.springjpa.example;

import com.ewa.springjpa.example.entities.Example;
import org.springframework.data.jpa.repository.JpaRepository;

/** Spring Data JPA repository — the TypeORM {@code Repository} / Tortoise queryset analog. */
public interface ExampleRepository extends JpaRepository<Example, Long> {}
