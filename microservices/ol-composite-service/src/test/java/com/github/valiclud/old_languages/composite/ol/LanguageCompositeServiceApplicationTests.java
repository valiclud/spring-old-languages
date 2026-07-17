package com.github.valiclud.old_languages.composite.ol;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_CONTENT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.HttpStatus.*;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.api.composite.core.recommendation.Recommendation;
import com.github.valiclud.api.composite.core.review.Review;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.api.exceptions.NotFoundException;
import com.github.valiclud.old_languages.composite.ol.services.OlCompositeIntegration;
import org.springframework.http.HttpStatus;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = RANDOM_PORT)
//@SpringBootTest
@AutoConfigureWebTestClient
//@ActiveProfiles("test")
class LanguageCompositeServiceApplicationTests {

	  private static final int LANGUAGE_ID_OK = 1;
	  private static final int LANGUAGE_ID_NOT_FOUND = 2;
	  private static final int LANGUAGE_ID_INVALID = -1;

	  @Autowired private WebTestClient client;

	  @MockitoBean private OlCompositeIntegration compositeIntegration;

	  @BeforeEach
	  void setUp() {

	    when(compositeIntegration.getOldLanguage(LANGUAGE_ID_OK))
	      .thenReturn(Mono.just(new OldLanguage(LANGUAGE_ID_OK, "name", 1, "mock-address")));
	    when(compositeIntegration.getRecommendations(LANGUAGE_ID_OK))
	      .thenReturn(Flux.fromIterable(singletonList(new Recommendation(LANGUAGE_ID_OK, 1, "author", 1, "content", "mock address"))));
	    when(compositeIntegration.getReviews(LANGUAGE_ID_OK))
	      .thenReturn(Flux.fromIterable(singletonList(new Review(LANGUAGE_ID_OK, 1, "author", "subject", "content", "mock address"))));

	    when(compositeIntegration.getOldLanguage(LANGUAGE_ID_NOT_FOUND))
	      .thenThrow(new NotFoundException("NOT FOUND: " + LANGUAGE_ID_NOT_FOUND));

	    when(compositeIntegration.getOldLanguage(LANGUAGE_ID_INVALID))
	      .thenThrow(new InvalidInputException("INVALID: " + LANGUAGE_ID_INVALID));
	  }

	  @Test
	  void contextLoads() {}

	  @Test
	  void getLanguageById() {

		  getAndVerifyProduct(LANGUAGE_ID_OK, OK)
	      .jsonPath("$.oldLanguageId").isEqualTo(LANGUAGE_ID_OK)
	      .jsonPath("$.recommendations.length()").isEqualTo(1)
	      .jsonPath("$.reviews.length()").isEqualTo(1);
	  }

	  @Test
	  void getLanguageNotFound() {

		  getAndVerifyProduct(LANGUAGE_ID_NOT_FOUND, NOT_FOUND)
	      .jsonPath("$.path").isEqualTo("/product-composite/" + LANGUAGE_ID_NOT_FOUND)
	      .jsonPath("$.message").isEqualTo("NOT FOUND: " + LANGUAGE_ID_NOT_FOUND);
	  }

	  @Test
	  void getLanguageInvalidInput() {

		  getAndVerifyProduct(LANGUAGE_ID_INVALID, UNPROCESSABLE_CONTENT)
	      .jsonPath("$.path").isEqualTo("/product-composite/" + LANGUAGE_ID_INVALID)
	      .jsonPath("$.message").isEqualTo("INVALID: " + LANGUAGE_ID_INVALID);
	  }
	  
	  private WebTestClient.BodyContentSpec getAndVerifyProduct(int productId, HttpStatus expectedStatus) {
		    return client.get()
		      .uri("/ol-composite/" + productId)
		      .accept(APPLICATION_JSON)
		      .exchange()
		      .expectStatus().isEqualTo(expectedStatus)
		      .expectHeader().contentType(APPLICATION_JSON)
		      .expectBody();
		  }
	}