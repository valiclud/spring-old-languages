package com.github.valiclud.old_languages.composite.ol.services;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;
import static com.github.valiclud.api.event.Event.Type.CREATE;
import static com.github.valiclud.api.event.Event.Type.DELETE;


import tools.jackson.databind.json.JsonMapper;
import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.api.composite.core.ol.OldLanguageService;
import com.github.valiclud.api.composite.core.recommendation.Recommendation;
import com.github.valiclud.api.composite.core.recommendation.RecommendationService;
import com.github.valiclud.api.composite.core.review.Review;
import com.github.valiclud.api.composite.core.review.ReviewService;
import com.github.valiclud.api.event.Event;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.api.exceptions.NotFoundException;
import com.github.valiclud.util.http.HttpErrorInfo;
import org.springframework.boot.health.contributor.Health;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

@Component
public class OlCompositeIntegration implements OldLanguageService, RecommendationService, ReviewService {

	  private static final Logger LOG = LoggerFactory.getLogger(OlCompositeIntegration.class);

	  private final WebClient webClient;
	  private JsonMapper mapper;
	  private RestTemplate restTemplate;

	  private final String productServiceUrl;
	  private final String recommendationServiceUrl;
	  private final String reviewServiceUrl;
	  
	  private StreamBridge streamBridge;

	  private Scheduler publishEventScheduler;

	  public OlCompositeIntegration(
	    RestTemplate restTemplate,
	    JsonMapper mapper,
	    @Value("${app.ol-service.host}") String productServiceHost,
	    @Value("${app.ol-service.port}") int productServicePort,
	    @Value("${app.recommendation-service.host}") String recommendationServiceHost,
	    @Value("${app.recommendation-service.port}") int recommendationServicePort,
	    @Value("${app.review-service.host}") String reviewServiceHost,
	    @Value("${app.review-service.port}") int reviewServicePort) {

		this.publishEventScheduler = publishEventScheduler;
		this.webClient = WebClient.create();
		this.mapper = mapper;
		this.streamBridge = streamBridge;

	    this.restTemplate = restTemplate;

	    productServiceUrl = "http://" + productServiceHost + ":" + productServicePort;
	    recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort;
	    reviewServiceUrl         = "http://" + reviewServiceHost + ":" + reviewServicePort;	  }

	  @Override
	  public Mono<OldLanguage> getOldLanguage(int oldLanguageId) {
		  String url = productServiceUrl + "/oldlanguage/" + oldLanguageId;
		    LOG.debug("Will call the getProduct API on URL: {}", url);

		    return webClient.get().uri(url).retrieve().bodyToMono(OldLanguage.class).log(LOG.getName(), FINE).onErrorMap(WebClientResponseException.class, ex -> handleException(ex));

	  }
	  
	  @Override
	  public Mono<OldLanguage> createOldLanguage(OldLanguage body) {

		  return Mono.fromCallable(() -> {
		      sendMessage("products-out-0", new Event<>(CREATE, body.getOldLanguageId(), body));
		      return body;
		    }).subscribeOn(publishEventScheduler);
	  }

	  @Override
	  public Mono<Void> deleteOldLanguage(int oldLanguageId) {
		  return Mono.fromRunnable(() -> sendMessage("products-out-0", new Event<>(DELETE, oldLanguageId, null)))
			      .subscribeOn(publishEventScheduler).then();
	  }
	  
	  @Override
	  public Mono<Recommendation> createRecommendation(Recommendation body) {

	    return Mono.fromCallable(() -> {
	      sendMessage("recommendations-out-0", new Event<>(CREATE, body.getProductId(), body));
	      return body;
	    }).subscribeOn(publishEventScheduler);
	  }

	  @Override
	  public Flux<Recommendation> getRecommendations(int productId) {

	    String url = recommendationServiceUrl + "/recommendation?productId=" + productId;

	    LOG.debug("Will call the getRecommendations API on URL: {}", url);

	    // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
	    return webClient.get().uri(url).retrieve().bodyToFlux(Recommendation.class).log(LOG.getName(), FINE).onErrorResume(error -> empty());
	  }

	  @Override
	  public Mono<Void> deleteRecommendations(int productId) {

	    return Mono.fromRunnable(() -> sendMessage("recommendations-out-0", new Event<>(DELETE, productId, null)))
	      .subscribeOn(publishEventScheduler).then();
	  }

	  @Override
	  public Mono<Review> createReview(Review body) {

	    return Mono.fromCallable(() -> {
	      sendMessage("reviews-out-0", new Event<>(CREATE, body.getProductId(), body));
	      return body;
	    }).subscribeOn(publishEventScheduler);
	  }

	  @Override
	  public Flux<Review> getReviews(int productId) {

	    String url = reviewServiceUrl + "/review?productId=" + productId;

	    LOG.debug("Will call the getReviews API on URL: {}", url);

	    // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
	    return webClient.get().uri(url).retrieve().bodyToFlux(Review.class).log(LOG.getName(), FINE).onErrorResume(error -> empty());
	  }

	  @Override
	  public Mono<Void> deleteReviews(int productId) {

	    return Mono.fromRunnable(() -> sendMessage("reviews-out-0", new Event<>(DELETE, productId, null)))
	      .subscribeOn(publishEventScheduler).then();
	  }
	  
	  public Mono<Health> getProductHealth() {
		    return getHealth(productServiceUrl);
		  }

		  public Mono<Health> getRecommendationHealth() {
		    return getHealth(recommendationServiceUrl);
		  }

		  public Mono<Health> getReviewHealth() {
		    return getHealth(reviewServiceUrl);
		  }

		  private Mono<Health> getHealth(String url) {
		    url += "/actuator/health";
		    LOG.debug("Will call the Health API on URL: {}", url);
		    return webClient.get().uri(url).retrieve().bodyToMono(String.class)
		      .map(s -> new Health.Builder().up().build())
		      .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
		      .log(LOG.getName(), FINE);
		  }

		  private void sendMessage(String bindingName, Event event) {
		    LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
		    Message message = MessageBuilder.withPayload(event)
		      .setHeader("partitionKey", event.getKey())
		      .build();
		    streamBridge.send(bindingName, message);
		  }

		  private Throwable handleException(Throwable ex) {

		    if (!(ex instanceof WebClientResponseException)) {
		      LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
		      return ex;
		    }

		    WebClientResponseException wcre = (WebClientResponseException)ex;

		    switch (HttpStatus.resolve(wcre.getStatusCode().value())) {

		      case NOT_FOUND:
		        return new NotFoundException(getErrorMessage(wcre));

		      case UNPROCESSABLE_CONTENT:
		        return new InvalidInputException(getErrorMessage(wcre));

		      default:
		        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
		        LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
		        return ex;
		    }
		  }

		  private String getErrorMessage(WebClientResponseException ex) {
		    return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		  }
		}