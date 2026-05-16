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

	    productServiceUrl = "http://" + productServiceHost + ":" + productServicePort + "/oldlanguage";
	    recommendationServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
	    reviewServiceUrl = "http://" + reviewServiceHost + ":" + reviewServicePort + "/review";
	  }

	  @Override
	  public OldLanguage getOldLanguage(int oldLanguageId) {
	    try {
	    	String url = productServiceUrl + "/" + oldLanguageId;
	      LOG.debug("Will call getProduct API on URL: {}", url);

	      OldLanguage ol = restTemplate.getForObject(url, OldLanguage.class);
	      LOG.debug("Found an ol with id: {}", ol.getOldLanguageId());

	      return ol;

	    } catch (HttpClientErrorException ex) {
	    	 throw handleHttpClientException(ex);
	    }
	  }
	  
	  @Override
	  public OldLanguage createOldLanguage(OldLanguage body) {

		    try {
		      String url = productServiceUrl;
		      LOG.debug("Will post a new product to URL: {}", url);

		      OldLanguage product = restTemplate.postForObject(url, body, OldLanguage.class);
		      LOG.debug("Created a product with id: {}", product.getOldLanguageId());

		      return product;

		    } catch (HttpClientErrorException ex) {
		      throw handleHttpClientException(ex);
		    }
	  }

	  @Override
	  public void deleteOldLanguage(int oldLanguageId) {
		  try {
			  String url = productServiceUrl + "/" + oldLanguageId;
		      LOG.debug("Will call the deleteProduct API on URL: {}", url);

		      restTemplate.delete(url);

		    } catch (HttpClientErrorException ex) {
		      throw handleHttpClientException(ex);
		    }
	  }
	  
	  @Override
	  public Recommendation createRecommendation(Recommendation body) {

	    try {
	      String url = recommendationServiceUrl;
	      LOG.debug("Will post a new recommendation to URL: {}", url);

	      Recommendation recommendation = restTemplate.postForObject(url, body, Recommendation.class);
	      LOG.debug("Created a recommendation with id: {}", recommendation.getProductId());

	      return recommendation;

	    } catch (HttpClientErrorException ex) {
	      throw handleHttpClientException(ex);
	    }
	  }
	  
	  @Override
	  public List<Recommendation> getRecommendations(int productId) {

	    try {
	    	String url = recommendationServiceUrl + "?productId=" + productId;

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

	  @Override
	  public void deleteRecommendations(int productId) {
	    try {
	      String url = recommendationServiceUrl + "?productId=" + productId;
	      LOG.debug("Will call the deleteRecommendations API on URL: {}", url);

	      restTemplate.delete(url);

	    } catch (HttpClientErrorException ex) {
	      throw handleHttpClientException(ex);
	    }
	  }
	  
	  @Override
	  public Review createReview(Review body) {

	    try {
	      String url = reviewServiceUrl;
	      LOG.debug("Will post a new review to URL: {}", url);

	      Review review = restTemplate.postForObject(url, body, Review.class);
	      LOG.debug("Created a review with id: {}", review.getProductId());

	      return review;

	    } catch (HttpClientErrorException ex) {
	      throw handleHttpClientException(ex);
	    }
	  }
	  
	  @Override
	  public List<Review> getReviews(int productId) {

	    try {
	      String url = reviewServiceUrl + "?productId=" + productId;

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

	  @Override
	  public void deleteReviews(int productId) {
	    try {
	      String url = reviewServiceUrl + "?productId=" + productId;
	      LOG.debug("Will call the deleteReviews API on URL: {}", url);

	      restTemplate.delete(url);

	    } catch (HttpClientErrorException ex) {
	      throw handleHttpClientException(ex);
	    }
	  }
	  
	  private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
		    switch (HttpStatus.resolve(ex.getStatusCode().value())) {

		      case NOT_FOUND:
		        return new NotFoundException(getErrorMessage(ex));

		      case UNPROCESSABLE_CONTENT:
		        return new InvalidInputException(getErrorMessage(ex));

		      default:
		        LOG.warn("Got an unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
		        LOG.warn("Error body: {}", ex.getResponseBodyAsString());
		        return ex;
		    }
		  }
	  
	  private String getErrorMessage(HttpClientErrorException ex) {
		      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
		  }

	}