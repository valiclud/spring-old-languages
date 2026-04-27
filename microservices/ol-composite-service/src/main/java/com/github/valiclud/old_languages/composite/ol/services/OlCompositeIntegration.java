package com.github.valiclud.old_languages.composite.ol.services;

import static org.springframework.http.HttpMethod.GET;

import tools.jackson.databind.json.JsonMapper;
import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.api.composite.core.ol.OldLanguageService;
import com.github.valiclud.api.composite.core.recommendation.Recommendation;
import com.github.valiclud.api.composite.core.recommendation.RecommendationService;
import com.github.valiclud.api.composite.core.review.Review;
import com.github.valiclud.api.composite.core.review.ReviewService;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.api.exceptions.NotFoundException;
import com.github.valiclud.util.http.HttpErrorInfo;

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

@Component
public class OlCompositeIntegration implements OldLanguageService, RecommendationService, ReviewService {

	  private static final Logger LOG = LoggerFactory.getLogger(OlCompositeIntegration.class);

	  private JsonMapper mapper;
	  private RestTemplate restTemplate;

	  private final String productServiceUrl;
	  private final String recommendationServiceUrl;
	  private final String reviewServiceUrl;

	  public OlCompositeIntegration(
	    RestTemplate restTemplate,
	    JsonMapper mapper,
	    @Value("${app.ol-service.host}") String productServiceHost,
	    @Value("${app.ol-service.port}") int productServicePort,
	    @Value("${app.recommendation-service.host}") String recommendationServiceHost,
	    @Value("${app.recommendation-service.port}") int recommendationServicePort,
	    @Value("${app.review-service.host}") String reviewServiceHost,
	    @Value("${app.review-service.port}") int reviewServicePort) {

	    this.restTemplate = restTemplate;
	    this.mapper = mapper;

	    productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/oldlanguage/";
	    recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation?productId=";
	    reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review?productId=";
	  }

	  public OldLanguage getOldLanguage(Long olId) {

	    try {
	      String url = productServiceUrl + olId;
	      LOG.debug("Will call getProduct API on URL: {}", url);

	      OldLanguage ol = restTemplate.getForObject(url, OldLanguage.class);
	      LOG.debug("Found an ol with id: {}", ol.getOldLanguageId());

	      return ol;

	    } catch (HttpClientErrorException ex) {

	      switch (HttpStatus.resolve(ex.getStatusCode().value())) {
	        case NOT_FOUND:
	          throw new NotFoundException(getErrorMessage(ex));

	        case UNPROCESSABLE_CONTENT:
	          throw new InvalidInputException(getErrorMessage(ex));

	        default:
	          LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
	          LOG.warn("Error body: {}", ex.getResponseBodyAsString());
	          throw ex;
	      }
	    }
	  }

	  private String getErrorMessage(HttpClientErrorException ex) {
	    return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
	  }

	  public List<Recommendation> getRecommendations(Long productId) {

	    try {
	      String url = recommendationServiceUrl + productId;

	      LOG.debug("Will call getRecommendations API on URL: {}", url);
	      List<Recommendation> recommendations = restTemplate
	        .exchange(url, GET, null, new ParameterizedTypeReference<List<Recommendation>>() {})
	        .getBody();

	      LOG.debug("Found {} recommendations for a product with id: {}", recommendations.size(), productId);
	      return recommendations;

	    } catch (Exception ex) {
	      LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
	      return new ArrayList<>();
	    }
	  }

	  public List<Review> getReviews(Long productId) {

	    try {
	      String url = reviewServiceUrl + productId;

	      LOG.debug("Will call getReviews API on URL: {}", url);
	      List<Review> reviews = restTemplate
	        .exchange(url, GET, null, new ParameterizedTypeReference<List<Review>>() {})
	        .getBody();

	      LOG.debug("Found {} reviews for a product with id: {}", reviews.size(), productId);
	      return reviews;

	    } catch (Exception ex) {
	      LOG.warn("Got an exception while requesting reviews, return zero reviews: {}", ex.getMessage());
	      return new ArrayList<>();
	    }
	  }
	}