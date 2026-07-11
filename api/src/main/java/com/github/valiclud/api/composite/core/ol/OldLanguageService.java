package com.github.valiclud.api.composite.core.ol;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

public interface OldLanguageService {

  Mono<OldLanguage> createOldLanguage(OldLanguage body);
	
  /**
   * Sample usage: "curl $HOST:$PORT/oldLanguage/1".
   *
   * @param oldLanguageId Id of the oldLanguage
   * @return the oldLanguage, if found, else null
   */
  @GetMapping(
    value = "/oldlanguage/{oldLanguageId}",
    produces = "application/json")
  Mono<OldLanguage> getOldLanguage(@PathVariable("oldLanguageId") int oldLanguageId);
  
  Mono<Void> deleteOldLanguage(int oldLanguageId);
}