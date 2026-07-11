package com.github.valiclud.old_languages.core.ol;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

import java.util.function.Consumer;

import static com.github.valiclud.api.event.Event.Type.CREATE;
import static com.github.valiclud.api.event.Event.Type.DELETE;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;


import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.api.event.Event;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.old_languages.core.ol.persistence.OldLanguageRepository;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebTestClient
class OldLanguageServiceApplicationTests extends MongoDbTestBase {

  @Autowired private WebTestClient client;

  @Autowired private OldLanguageRepository repository;
  
  
  @Autowired
  @Qualifier("messageProcessor")
  private Consumer<Event<Integer, OldLanguage>> messageProcessor;

  @BeforeEach
  void setupDb() {
	  repository.deleteAll().block();
  }

  @Test
  void getProductById() {

    int productId = 1;

    assertNull(repository.findByOldLanguageId(productId).block());
    assertEquals(0, (long)repository.count().block());

    sendCreateProductEvent(productId);

    assertNotNull(repository.findByOldLanguageId(productId).block());
    assertEquals(1, (long)repository.count().block());

    getAndVerifyProduct(productId, OK)
      .jsonPath("$.oldLanguageId").isEqualTo(productId);  }

  @Test
  void duplicateError() {

    int productId = 1;

    assertNull(repository.findByOldLanguageId(productId).block());

    sendCreateProductEvent(productId);

    assertNotNull(repository.findByOldLanguageId(productId).block());

    InvalidInputException thrown = assertThrows(
      InvalidInputException.class,
      () -> sendCreateProductEvent(productId),
      "Expected a InvalidInputException here!");
    assertEquals("Duplicate key, OldLanguage Id: " + productId, thrown.getMessage());  }

  @Test
  void deleteProduct() {

    int productId = 1;

    sendCreateProductEvent(productId);
    assertNotNull(repository.findByOldLanguageId(productId).block());

    sendDeleteProductEvent(productId);
    assertNull(repository.findByOldLanguageId(productId).block());

    sendDeleteProductEvent(productId);
  }

  @Test
  void getProductInvalidParameterString() {

    getAndVerifyProduct("/no-integer", BAD_REQUEST)
      .jsonPath("$.path").isEqualTo("/oldlanguage/no-integer")
      .jsonPath("$.message").isEqualTo("Type mismatch.");
  }

  @Test
  void getProductNotFound() {

    int productIdNotFound = 13;
    getAndVerifyProduct(productIdNotFound, NOT_FOUND)
      .jsonPath("$.path").isEqualTo("/oldlanguage/" + productIdNotFound)
      .jsonPath("$.message").isEqualTo("No oldLanguage found for oldLanguageId: " + productIdNotFound);
  }

  @Test
  void getProductInvalidParameterNegativeValue() {

    int productIdInvalid = -1;

    getAndVerifyProduct(productIdInvalid, UNPROCESSABLE_CONTENT)
      .jsonPath("$.path").isEqualTo("/oldlanguage/" + productIdInvalid)
      .jsonPath("$.message").isEqualTo("Invalid productId: " + productIdInvalid);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
    return getAndVerifyProduct("/" + productId, expectedStatus);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(String productIdPath, HttpStatus expectedStatus) {
    return client.get()
      .uri("/oldlanguage" + productIdPath)
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isEqualTo(expectedStatus)
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody();
  }

  private WebTestClient.BodyContentSpec postAndVerifyProduct(int productId, HttpStatus expectedStatus) {
    OldLanguage product = new OldLanguage(productId, "Name " + productId, productId, "SA");
    return client.post()
      .uri("/oldlanguage")
      .body(just(product), OldLanguage.class)
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isEqualTo(expectedStatus)
      .expectHeader().contentType(APPLICATION_JSON)
      .expectBody();
  }

  private WebTestClient.BodyContentSpec deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
    return client.delete()
      .uri("/oldlanguage/" + productId)
      .accept(APPLICATION_JSON)
      .exchange()
      .expectStatus().isEqualTo(expectedStatus)
      .expectBody();
  }
  
  private void sendCreateProductEvent(int productId) {
	    OldLanguage product = new OldLanguage(productId, "Name " + productId, productId, "SA");
	    Event<Integer, OldLanguage> event = new Event<>(CREATE, productId, product);
	    messageProcessor.accept(event);
	  }

	  private void sendDeleteProductEvent(int productId) {
	    Event<Integer, OldLanguage> event = new Event<>(DELETE, productId, null);
	    messageProcessor.accept(event);
	  }
}