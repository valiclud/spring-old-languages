package com.github.valiclud.old_languages.composite.ol.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.api.composite.core.recommendation.Recommendation;
import com.github.valiclud.api.composite.core.review.Review;
import com.github.valiclud.api.composite.ol.OldLanguageAggregate;
import com.github.valiclud.api.composite.ol.OldLanguageCompositeService;
import com.github.valiclud.api.composite.ol.RecommendationSummary;
import com.github.valiclud.api.composite.ol.ReviewSummary;
import com.github.valiclud.api.composite.ol.ServiceAddresses;
import com.github.valiclud.api.exceptions.NotFoundException;
import com.github.valiclud.util.http.ServiceUtil;

@RestController
public class OLCompositeServiceImpl implements OldLanguageCompositeService {

	  private final ServiceUtil serviceUtil;
	  private OlCompositeIntegration integration;

	  public OLCompositeServiceImpl(
	    ServiceUtil serviceUtil, OlCompositeIntegration integration) {
	    
	    this.serviceUtil = serviceUtil;
	    this.integration = integration;
	  }

	  @Override
	  public OldLanguageAggregate getProduct(Long oldlanguageId) {
	    OldLanguage product = integration.getOldLanguage(oldlanguageId);
	    if (product == null) {
	      throw new NotFoundException("No ol found for oldlanguageId: " + oldlanguageId);
	    }
	    List<Recommendation> recommendations = integration.getRecommendations(oldlanguageId);

	    List<Review> reviews = integration.getReviews(oldlanguageId);

	    return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
	  }

	  private OldLanguageAggregate createProductAggregate(
	    OldLanguage ol,
	    List<Recommendation> recommendations,
	    List<Review> reviews,
	    String serviceAddress) {

	    // 1. Setup product info
	    Long productId = ol.getOldLanguageId();
	    String name = ol.getName();
	    int weight = ol.getWeight();

	    // 2. Copy summary recommendation info, if available
	    List<RecommendationSummary> recommendationSummaries =
	      (recommendations == null) ? null : recommendations.stream()
	        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate()))
	        .collect(Collectors.toList());

	    // 3. Copy summary review info, if available
	    List<ReviewSummary> reviewSummaries = 
	      (reviews == null) ? null : reviews.stream()
	        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject()))
	        .collect(Collectors.toList());

	    // 4. Create info regarding the involved microservices addresses
	    String productAddress = ol.getServiceAddress();
	    String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
	    String recommendationAddress = (recommendations != null && recommendations.size() > 0) ? recommendations.get(0).getServiceAddress() : "";
	    ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

	    return new OldLanguageAggregate(productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
	  }
	}