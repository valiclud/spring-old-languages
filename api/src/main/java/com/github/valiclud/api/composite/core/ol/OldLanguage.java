package com.github.valiclud.api.composite.core.ol;

public class OldLanguage {
	  private Long oldLanguageId;
	  private String name;
	  private int weight;
	  private String serviceAddress;

	  public OldLanguage() {
	    oldLanguageId = 0L;
	    name = null;
	    weight = 0;
	    serviceAddress = null;
	  }

	  public OldLanguage(Long oldLanguageId, String name, int weight, String serviceAddress) {
	    this.oldLanguageId = oldLanguageId;
	    this.name = name;
	    this.weight = weight;
	    this.serviceAddress = serviceAddress;
	  }

	  public Long getOldLanguageId() {
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

	  public void setOldLanguageId(Long oldLanguageId) {
		  this.oldLanguageId = oldLanguageId;
	  }

	  public void setName(String name) {
		  this.name = name;
	  }

	  public void setWeight(int weight) {
		  this.weight = weight;
	  }

	  public void setServiceAddress(String serviceAddress) {
		  this.serviceAddress = serviceAddress;
	  }
	  
	}
