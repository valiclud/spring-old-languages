package com.github.valiclud.api.composite.ol;

import java.util.List;

public class OldLanguageAggregate {
	  private int oldLanguageId;
	  private String name;
	  private int weight;
	  private List<RecommendationSummary> recommendations;
	  private List<ReviewSummary> reviews;
	  private ServiceAddresses serviceAddresses;

	  public OldLanguageAggregate() {
		  oldLanguageId = 0;
		    name = null;
		    weight = 0;
		    recommendations = null;
		    reviews = null;
		    serviceAddresses = null;
		  }
	  
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

	  public void setOldLanguageId(int oldLanguageId) {
		  this.oldLanguageId = oldLanguageId;
	  }

	  public void setName(String name) {
		  this.name = name;
	  }

	  public void setWeight(int weight) {
		  this.weight = weight;
	  }

	  public void setRecommendations(List<RecommendationSummary> recommendations) {
		  this.recommendations = recommendations;
	  }

	  public void setReviews(List<ReviewSummary> reviews) {
		  this.reviews = reviews;
	  }

	  public void setServiceAddresses(ServiceAddresses serviceAddresses) {
		  this.serviceAddresses = serviceAddresses;
	  }
	}