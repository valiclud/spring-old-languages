package com.github.valiclud.old_languages.core.ol.persistence;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface OldLanguageRepository extends PagingAndSortingRepository<OldLanguageEntity, String>, CrudRepository<OldLanguageEntity, String> {
	  Optional<OldLanguageEntity> findByOldLanguageId(int olId);
	}
