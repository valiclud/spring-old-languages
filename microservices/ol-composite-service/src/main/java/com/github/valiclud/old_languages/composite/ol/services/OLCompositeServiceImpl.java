package com.github.valiclud.old_languages.composite.ol.services;

import static java.util.logging.Level.FINE;

import java.util.ArrayList;
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

import reactor.core.publisher.Mono;

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
	  public Mono<Void> createProduct(OldLanguageAggregate body) {
	    try {
	      List<Mono> monoList = new ArrayList<>();
	      LOG.debug("createCompositeProduct: creates a new composite entity for productId: {}", body.getOldLanguageId());

	      OldLanguage product = new OldLanguage(body.getOldLanguageId(), body.getName(), body.getWeight(), null);
	      monoList.add(integration.createOldLanguage(product));

	      if (body.getRecommendations() != null) {
	        body.getRecommendations().forEach(r -> {
	          Recommendation recommendation = new Recommendation(body.getOldLanguageId(), r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent(), null);
	          monoList.add(integration.createRecommendation(recommendation));
	        });
	      }

	      if (body.getReviews() != null) {
	        body.getReviews().forEach(r -> {
	          Review review = new Review(body.getOldLanguageId(), r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent(), null);
	          monoList.add(integration.createReview(review));
	        });
	      }

	      LOG.debug("createCompositeProduct: composite entities created for productId: {}", body.getOldLanguageId());

	      return Mono.zip(r -> "", monoList.toArray(new Mono[0]))
	        .doOnError(ex -> LOG.warn("createCompositeProduct failed: {}", ex.toString()))
	        .then();

	    } catch (RuntimeException re) {
	      LOG.warn("createCompositeProduct failed: {}", re.toString());
	      throw re;
	    }
	  }

	@Override
	public Mono<OldLanguageAggregate> getProduct(int oldlanguageId) {
		LOG.debug("getCompositeProduct: lookup a product aggregate for productId: {}", oldlanguageId);

		LOG.info("Will get composite product info for product.id={}", oldlanguageId);
	    return Mono.zip(
	      values -> createProductAggregate((OldLanguage) values[0], (List<Recommendation>) values[1], (List<Review>) values[2], serviceUtil.getServiceAddress()),
	      integration.getOldLanguage(oldlanguageId),
	      integration.getRecommendations(oldlanguageId).collectList(),
	      integration.getReviews(oldlanguageId).collectList())
	      .doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
	      .log(LOG.getName(), FINE);	}

	@Override
	  public Mono<Void> deleteProduct(int productId) {

		try {

		      LOG.info("Will delete a product aggregate for product.id: {}", productId);

		      return Mono.zip(
		        r -> "",
		        integration.deleteOldLanguage(productId),
		        integration.deleteRecommendations(productId),
		        integration.deleteReviews(productId))
		        .doOnError(ex -> LOG.warn("delete failed: {}", ex.toString()))
		        .log(LOG.getName(), FINE).then();

		    } catch (RuntimeException re) {
		      LOG.warn("deleteCompositeProduct failed: {}", re.toString());
		      throw re;
		    }
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