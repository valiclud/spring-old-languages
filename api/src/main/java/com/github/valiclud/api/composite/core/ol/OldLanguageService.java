package com.github.valiclud.api.composite.core.ol;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface OldLanguageService {

 /**
   * Sample usage, see below.
   *
   * curl -X POST $HOST:$PORT/oldlanguage \
   *   -H "Content-Type: application/json" --data \
   *   '{"oldLanguageId":123,"name":"oldLanguage 123","weight":123}'
   *
   * @param body A JSON representation of the new OldLanguage
   * @return A JSON representation of the newly created OldLanguage
   */
  @PostMapping(
    value    = "/oldlanguage",
    consumes = "application/json",
    produces = "application/json")
  OldLanguage createOldLanguage(@RequestBody OldLanguage body);
	
  /**
   * Sample usage: "curl $HOST:$PORT/oldLanguage/1".
   *
   * @param oldLanguageId Id of the oldLanguage
   * @return the oldLanguage, if found, else null
   */
  @GetMapping(
    value = "/oldlanguage/{oldLanguageId}",
    produces = "application/json")
  OldLanguage getOldLanguage(@PathVariable("oldLanguageId") int oldLanguageId);
  
  /**
   * Sample usage: "curl -X DELETE $HOST:$PORT/oldlanguage/1".
   *
   * @param oldLanguageId Id of the oldlanguage
   */
  @DeleteMapping(value = "/oldlanguage/{oldLanguageId}")
  void deleteOldLanguage(@PathVariable("oldLanguageId") int oldLanguageId);
}