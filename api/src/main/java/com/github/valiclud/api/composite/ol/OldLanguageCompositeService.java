package com.github.valiclud.api.composite.ol;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface OldLanguageCompositeService {

  /**
   * Sample usage: "curl $HOST:$PORT/product-composite/1".
   *
   * @param oldlanguageId Id of the OldLanguage
   * @return the composite OldLanguage info, if found, else null
   */
  @GetMapping(
    value = "/ol-composite/{oldLanguageId}",
    produces = "application/json")
  OldLanguageAggregate getProduct(@PathVariable("oldLanguageId") Long oldLanguageId);
}