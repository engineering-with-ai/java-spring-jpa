package com.ewa.springjpa.example;

import com.ewa.springjpa.example.dto.CreateExampleRequest;
import com.ewa.springjpa.example.dto.ExampleResponse;
import com.ewa.springjpa.example.dto.UpdateExampleRequest;
import com.ewa.springjpa.example.entities.Example;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Business logic for the example resource. Mirrors {@code ExampleService} in the siblings. */
@Service
public class ExampleService {

  private final ExampleRepository repository;

  public ExampleService(ExampleRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public ExampleResponse create(CreateExampleRequest request) {
    return ExampleResponse.from(repository.save(new Example(request.name())));
  }

  public List<ExampleResponse> findAll() {
    return repository.findAll().stream().map(ExampleResponse::from).toList();
  }

  public Optional<ExampleResponse> findById(long id) {
    return repository.findById(id).map(ExampleResponse::from);
  }

  @Transactional
  public Optional<ExampleResponse> update(long id, UpdateExampleRequest request) {
    return repository
        .findById(id)
        .map(
            example -> {
              if (request.name() != null) {
                example.setName(request.name());
              }
              return ExampleResponse.from(repository.save(example));
            });
  }

  @Transactional
  public boolean delete(long id) {
    if (!repository.existsById(id)) {
      return false;
    }
    repository.deleteById(id);
    return true;
  }
}
