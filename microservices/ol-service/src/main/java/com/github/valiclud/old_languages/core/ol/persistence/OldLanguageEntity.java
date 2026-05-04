package com.github.valiclud.old_languages.core.ol.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "oldlanguages")
public class OldLanguageEntity {

  @Id private String id;

  @Version private Integer version;

  @Indexed(unique = true)
  private int oldLanguageId;

  private String name;
  private int weight;

  public OldLanguageEntity() {}

  public OldLanguageEntity(int oldLanguageId, String name, int weight) {
    this.oldLanguageId = oldLanguageId;
    this.name = name;
    this.weight = weight;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public int getOldLanguageId() {
    return oldLanguageId;
  }

  public void setOldLanguageId(int oldLanguageId) {
    this.oldLanguageId = oldLanguageId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }
}