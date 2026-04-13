package com.github.valiclud.api.composite.core.ol;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface OldLanguageService {

  /**
   * Sample usage: "curl $HOST:$PORT/product/1".
   *
   * @param productId Id of the product
   * @return the product, if found, else null
   */
  @GetMapping(
    value = "/oldlanguage/{oldLanguageId}",
    produces = "application/json")
  OldLanguage getOldLanguage(@PathVariable("oldLanguageId") Long oldLanguageId);
}