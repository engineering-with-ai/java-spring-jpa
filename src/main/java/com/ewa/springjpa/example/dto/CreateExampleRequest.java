package com.ewa.springjpa.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Body for {@code POST /example}. Jakarta Bean Validation — the nestjs-zod / Pydantic analog. */
public record CreateExampleRequest(@NotBlank @Size(max = 100) String name) {}
