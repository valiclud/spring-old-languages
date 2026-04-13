package com.github.valiclud.api.composite.core.ol;

public class OldLanguage {
	  private final Long oldLanguageId;
	  private final String name;
	  private final int weight;
	  private final String serviceAddress;

	  public OldLanguage() {
	    oldLanguageId = 0L;
	    name = null;
	    weight = 0;
	    serviceAddress = null;
	  }

	  public OldLanguage(Long oldLanguageId2, String name, int weight, String serviceAddress) {
	    this.oldLanguageId = oldLanguageId2;
	    this.name = name;
	    this.weight = weight;
	    this.serviceAddress = serviceAddress;
	  }

	  public Long geOldLanguageId() {
	    return oldLanguageId;
	  }

	  public String getName() {
	    return name;
	  }

	  public int getWeight() {
	    return weight;
	  }

	  public String getServiceAddress() {
	    return serviceAddress;
	  }
	}
