package com.github.valiclud.old_languages.core.ol.services;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.old_languages.core.ol.persistence.OldLanguageEntity;

@Mapper(componentModel = "spring")
public interface OldLanguageMapper {

  @Mappings({
    @Mapping(target = "serviceAddress", ignore = true)
  })
  OldLanguage entityToApi(OldLanguageEntity entity);

  @Mappings({
    @Mapping(target = "id", ignore = true), @Mapping(target = "version", ignore = true)
  })
  OldLanguageEntity apiToEntity(OldLanguage api);
}