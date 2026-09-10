package com.ewa.springjpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.ewa.springjpa.example.ExampleService;
import com.ewa.springjpa.example.dto.CreateExampleRequest;
import com.ewa.springjpa.example.dto.ExampleResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Example resource over real HTTP against a real Postgres — the {@code example-resource.test.ts}
 * analog.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@Testcontainers(disabledWithoutDocker = true)
class ExampleResourceIT {

  @LocalServerPort int port;
  @Autowired ExampleService exampleService;
  RestTestClient rest;

  @BeforeEach
  void bindClient() {
    rest = RestTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  void createsAndRetrievesOverHttp() {
    // Arrange: seed through the service layer, like the siblings do
    ExampleResponse seeded = exampleService.create(new CreateExampleRequest("Test Example Item"));

    // Act + Assert
    rest.get()
        .uri("/example/{id}", seeded.id())
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody()
        .jsonPath("$.name")
        .isEqualTo("Test Example Item");
  }

  @Test
  void returns404ForMissingId() {
    rest.get().uri("/example/{id}", 999_999).exchange().expectStatus().isNotFound();
  }

  @Test
  void rejectsBlankNameWith400() {
    rest.post()
        .uri("/example")
        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
        .body("{\"name\":\"\"}")
        .exchange()
        .expectStatus()
        .isBadRequest();
  }

  @Test
  void createThenListContainsIt() {
    // Arrange
    exampleService.create(new CreateExampleRequest("listed-item"));

    // Act
    List<ExampleResponse> all =
        rest.get()
            .uri("/example")
            .exchange()
            .expectStatus()
            .isOk()
            .expectBody(
                new org.springframework.core.ParameterizedTypeReference<List<ExampleResponse>>() {})
            .returnResult()
            .getResponseBody();

    // Assert
    assertThat(all).extracting(ExampleResponse::name).contains("listed-item");
  }
}
