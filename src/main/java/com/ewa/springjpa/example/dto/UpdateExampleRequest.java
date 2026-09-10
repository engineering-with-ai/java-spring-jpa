package com.ewa.springjpa.example.dto;

import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/** Body for {@code PATCH /example/{id}}. Fields are optional — a partial update. */
public record UpdateExampleRequest(@Size(max = 100) @Nullable String name) {}
