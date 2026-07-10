package com.github.valiclud.old_languages.core.ol.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;


public interface OldLanguageRepository extends ReactiveCrudRepository<OldLanguageEntity, String> {
	  Mono<OldLanguageEntity> findByOldLanguageId(int olId);
	}
