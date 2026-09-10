package com.ewa.springjpa.example;

import com.ewa.springjpa.example.dto.CreateExampleRequest;
import com.ewa.springjpa.example.dto.ExampleResponse;
import com.ewa.springjpa.example.dto.UpdateExampleRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** REST endpoints for the example resource. Thin — all logic lives in {@link ExampleService}. */
@Tag(name = "example")
@RestController
@RequestMapping("/example")
public class ExampleController {

  private final ExampleService service;

  public ExampleController(ExampleService service) {
    this.service = service;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ExampleResponse create(@Valid @RequestBody CreateExampleRequest request) {
    return service.create(request);
  }

  @GetMapping
  public List<ExampleResponse> findAll() {
    return service.findAll();
  }

  @GetMapping("/{id}")
  public ExampleResponse findOne(@PathVariable long id) {
    return service.findById(id).orElseThrow(ExampleController::notFound);
  }

  @PatchMapping("/{id}")
  public ExampleResponse update(
      @PathVariable long id, @Valid @RequestBody UpdateExampleRequest request) {
    return service.update(id, request).orElseThrow(ExampleController::notFound);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void remove(@PathVariable long id) {
    if (!service.delete(id)) {
      throw notFound();
    }
  }

  private static ResponseStatusException notFound() {
    return new ResponseStatusException(HttpStatus.NOT_FOUND);
  }
}
