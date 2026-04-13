package com.github.valiclud.api.composite.ol;

import java.util.List;

public class OldLanguageAggregate {
	  private final int oldLanguageId;
	  private final String name;
	  private final int weight;
	  private final List<RecommendationSummary> recommendations;
	  private final List<ReviewSummary> reviews;
	  private final ServiceAddresses serviceAddresses;

	  public OldLanguageAggregate(
	    int oldLanguageId,
	    String name,
	    int weight,
	    List<RecommendationSummary> recommendations,
	    List<ReviewSummary> reviews,
	    ServiceAddresses serviceAddresses) {

	    this.oldLanguageId = oldLanguageId;
	    this.name = name;
	    this.weight = weight;
	    this.recommendations = recommendations;
	    this.reviews = reviews;
	    this.serviceAddresses = serviceAddresses;
	  }

	  public int getOldLanguageId() {
	    return oldLanguageId;
	  }

	  public String getName() {
	    return name;
	  }

	  public int getWeight() {
	    return weight;
	  }

	  public List<RecommendationSummary> getRecommendations() {
	    return recommendations;
	  }

	  public List<ReviewSummary> getReviews() {
	    return reviews;
	  }

	  public ServiceAddresses getServiceAddresses() {
	    return serviceAddresses;
	  }
	}