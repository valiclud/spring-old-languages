package com.github.valiclud.old_languages.core.ol;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.old_languages.core.ol.persistence.OldLanguageEntity;
import com.github.valiclud.old_languages.core.ol.services.OldLanguageMapper;

@SpringBootTest
class MapperTests {
  
  //@Spy
  private OldLanguageMapper mapper = Mappers.getMapper(OldLanguageMapper.class);
  
  @Test
  void mapperTests() {

    assertNotNull(mapper);

    OldLanguage api = new OldLanguage(1, "n", 1, "sa");

    OldLanguageEntity entity = mapper.apiToEntity(api);

    assertEquals(api.getOldLanguageId(), entity.getOldLanguageId());
    assertEquals(api.getName(), entity.getName());
    assertEquals(api.getWeight(), entity.getWeight());

    OldLanguage api2 = mapper.entityToApi(entity);

    assertEquals(api.getOldLanguageId(), api2.getOldLanguageId());
    assertEquals(api.getName(),      api2.getName());
    assertEquals(api.getWeight(),    api2.getWeight());
    assertNull(api2.getServiceAddress());
  }
}