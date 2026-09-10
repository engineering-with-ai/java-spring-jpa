package com.ewa.springjpa.example.dto;

import com.ewa.springjpa.example.entities.Example;

/** Response shape for the example endpoints — the JPA entity never leaves the service layer. */
public record ExampleResponse(Long id, String name) {

  public static ExampleResponse from(Example example) {
    return new ExampleResponse(example.getId(), example.getName());
  }
}
