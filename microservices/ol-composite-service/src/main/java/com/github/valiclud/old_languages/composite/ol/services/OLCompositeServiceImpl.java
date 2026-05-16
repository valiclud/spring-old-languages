package com.github.valiclud.old_languages.composite.ol.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

	private static final Logger LOG = LoggerFactory.getLogger(OLCompositeServiceImpl.class);

	private final ServiceUtil serviceUtil;
	private OlCompositeIntegration integration;

	public OLCompositeServiceImpl(ServiceUtil serviceUtil, OlCompositeIntegration integration) {

		this.serviceUtil = serviceUtil;
		this.integration = integration;
	}

	@Override
	  public void createProduct(OldLanguageAggregate body) {
	    try {
	      LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getOldLanguageId());

	      OldLanguage product = new OldLanguage(body.getOldLanguageId(), body.getName(), body.getWeight(), null);
	      integration.createOldLanguage(product);

	      if (body.getRecommendations() != null) {
	        body.getRecommendations().forEach(r -> {
	          Recommendation recommendation = new Recommendation(body.getOldLanguageId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
	          integration.createRecommendation(recommendation);
	        });
	      }

	      if (body.getReviews() != null) {
	        body.getReviews().forEach(r -> {
	          Review review = new Review(body.getOldLanguageId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
	          integration.createReview(review);
	        });
	      }

	      LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.getOldLanguageId());

	    } catch (RuntimeException re) {
	      LOG.warn("createCompositeProduct failed", re);
	      throw re;
	    }
	  }

	@Override
	public OldLanguageAggregate getProduct(int oldlanguageId) {
		LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", oldlanguageId);

		OldLanguage product = integration.getOldLanguage(oldlanguageId);
		if (product == null) {
			throw new NotFoundException("No ol found for oldlanguageId: " + oldlanguageId);
		}
		List<Recommendation> recommendations = integration.getRecommendations(oldlanguageId);

		List<Review> reviews = integration.getReviews(oldlanguageId);

		LOG.debug("getCompositeProduct: aggregate entity found for productId: {}", oldlanguageId);

		return createProductAggregate(product, recommendations, reviews, serviceUtil.getServiceAddress());
	}

	@Override
	  public void deleteProduct(int productId) {

	    LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

	    integration.deleteOldLanguage(productId);

	    integration.deleteRecommendations(productId);

	    integration.deleteReviews(productId);

	    LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);
	  }
	
	private OldLanguageAggregate createProductAggregate(OldLanguage ol, List<Recommendation> recommendations,
			List<Review> reviews, String serviceAddress) {

		// 1. Setup product info
		int productId = ol.getOldLanguageId();
		String name = ol.getName();
		int weight = ol.getWeight();

		// 2. Copy summary recommendation info, if available
	    List<RecommendationSummary> recommendationSummaries = (recommendations == null) ? null :
	      recommendations.stream()
	        .map(r -> new RecommendationSummary(r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
	        .collect(Collectors.toList());

	    // 3. Copy summary review info, if available
	    List<ReviewSummary> reviewSummaries = (reviews == null) ? null :
	      reviews.stream()
	        .map(r -> new ReviewSummary(r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
	        .collect(Collectors.toList());

		// 4. Create info regarding the involved microservices addresses
		String productAddress = ol.getServiceAddress();
		String reviewAddress = (reviews != null && reviews.size() > 0) ? reviews.get(0).getServiceAddress() : "";
		String recommendationAddress = (recommendations != null && recommendations.size() > 0)
				? recommendations.get(0).getServiceAddress()
				: "";
		ServiceAddresses serviceAddresses = new ServiceAddresses(serviceAddress, productAddress, reviewAddress,
				recommendationAddress);

		return new OldLanguageAggregate(productId, name, weight, recommendationSummaries, reviewSummaries,
				serviceAddresses);
	}
}