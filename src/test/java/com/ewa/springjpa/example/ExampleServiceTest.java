package com.ewa.springjpa.example;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.ewa.springjpa.example.dto.CreateExampleRequest;
import com.ewa.springjpa.example.dto.ExampleResponse;
import com.ewa.springjpa.example.dto.UpdateExampleRequest;
import com.ewa.springjpa.example.entities.Example;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

/** Unit — mocked repository, AAA. Mirrors {@code example.service.test.ts} one-for-one. */
@ExtendWith(MockitoExtension.class)
class ExampleServiceTest {

  @Mock private ExampleRepository repository;
  @InjectMocks private ExampleService service;

  private static Example withId(long id, String name) {
    Example example = new Example(name);
    ReflectionTestUtils.setField(example, "id", id);
    return example;
  }

  @Test
  void createSavesAndReturnsResponse() {
    // Arrange
    given(repository.save(any(Example.class))).willReturn(withId(1, "bar"));

    // Act
    ExampleResponse result = service.create(new CreateExampleRequest("bar"));

    // Assert
    assertThat(result).isEqualTo(new ExampleResponse(1L, "bar"));
  }

  @Test
  void findAllReturnsEveryRow() {
    // Arrange
    given(repository.findAll()).willReturn(List.of(withId(1, "a"), withId(2, "b")));

    // Act
    List<ExampleResponse> result = service.findAll();

    // Assert
    assertThat(result).extracting(ExampleResponse::name).containsExactly("a", "b");
  }

  @Test
  void findByIdReturnsResponseWhenPresent() {
    // Arrange
    given(repository.findById(1L)).willReturn(Optional.of(withId(1, "a")));

    // Act
    Optional<ExampleResponse> result = service.findById(1);

    // Assert
    assertThat(result).contains(new ExampleResponse(1L, "a"));
  }

  @Test
  void findByIdIsEmptyWhenMissing() {
    // Arrange
    given(repository.findById(999L)).willReturn(Optional.empty());

    // Act
    Optional<ExampleResponse> result = service.findById(999);

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  void updateAppliesNameWhenPresent() {
    // Arrange
    given(repository.findById(1L)).willReturn(Optional.of(withId(1, "old")));
    given(repository.save(any(Example.class))).willAnswer(invocation -> invocation.getArgument(0));

    // Act
    Optional<ExampleResponse> result = service.update(1, new UpdateExampleRequest("new"));

    // Assert
    assertThat(result).contains(new ExampleResponse(1L, "new"));
  }

  @Test
  void updateIsEmptyWhenMissing() {
    // Arrange
    given(repository.findById(999L)).willReturn(Optional.empty());

    // Act
    Optional<ExampleResponse> result = service.update(999, new UpdateExampleRequest("x"));

    // Assert
    assertThat(result).isEmpty();
  }

  @Test
  void deleteReturnsTrueWhenPresent() {
    // Arrange
    given(repository.existsById(1L)).willReturn(true);

    // Act
    boolean result = service.delete(1);

    // Assert
    assertThat(result).isTrue();
  }

  @Test
  void deleteReturnsFalseWhenMissing() {
    // Arrange
    given(repository.existsById(999L)).willReturn(false);

    // Act
    boolean result = service.delete(999);

    // Assert
    assertThat(result).isFalse();
  }
}
