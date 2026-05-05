package com.github.valiclud.api.composite.ol;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "OldLanguageComposite", description = "REST API for composite ol information.")
public interface OldLanguageCompositeService {

  /**
   * Sample usage: "curl $HOST:$PORT/ol-composite/1".
   *
   * @param oldlanguageId Id of the OldLanguage
   * @return the composite OldLanguage info, if found, else null
   */
  @Operation(
	    summary = "${api.ol-composite.get-composite-ol.description}",
	    description = "${api.ol-composite.get-composite-ol.notes}")
	    @ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "${api.responseCodes.ok.description}"),
	    @ApiResponse(responseCode = "400", description = "${api.responseCodes.badRequest.description}"),
	    @ApiResponse(responseCode = "404", description = "${api.responseCodes.notFound.description}"),
	    @ApiResponse(responseCode = "422", description = "${api.responseCodes.unprocessableEntity.description}")
	  })
  @GetMapping(
    value = "/ol-composite/{oldLanguageId}",
    produces = "application/json")
  OldLanguageAggregate getProduct(@PathVariable("oldLanguageId") int oldLanguageId);
}