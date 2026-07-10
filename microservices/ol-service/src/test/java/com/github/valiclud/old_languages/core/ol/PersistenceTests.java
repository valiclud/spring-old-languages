package com.github.valiclud.old_languages.core.ol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import reactor.test.StepVerifier;

import com.github.valiclud.old_languages.core.ol.persistence.OldLanguageEntity;
import com.github.valiclud.old_languages.core.ol.persistence.OldLanguageRepository;

@DataMongoTest
class PersistenceTests extends MongoDbTestBase {

  @Autowired
  private OldLanguageRepository repository;

  private OldLanguageEntity savedEntity;

  @BeforeEach
  void setupDb() {
	  StepVerifier.create(repository.deleteAll()).verifyComplete();


    OldLanguageEntity entity = new OldLanguageEntity(1, "n", 1);
    StepVerifier.create(repository.save(entity))
    .expectNextMatches(createdEntity -> {
      savedEntity = createdEntity;
      return areProductEqual(entity, savedEntity);
    })
    .verifyComplete();
  }


  @Test
  void create() {

    OldLanguageEntity newEntity = new OldLanguageEntity(2, "n", 2);
    
    StepVerifier.create(repository.save(newEntity))
    .expectNextMatches(createdEntity -> newEntity.getOldLanguageId() == createdEntity.getOldLanguageId())
    .verifyComplete();

  StepVerifier.create(repository.findById(newEntity.getId()))
    .expectNextMatches(foundEntity -> areProductEqual(newEntity, foundEntity))
    .verifyComplete();

  StepVerifier.create(repository.count()).expectNext(2L).verifyComplete();
  }

  @Test
  void update() {
	  savedEntity.setName("n2");
	  StepVerifier.create(repository.save(savedEntity))
      .expectNextMatches(updatedEntity -> updatedEntity.getName().equals("n2"))
      .verifyComplete();

    StepVerifier.create(repository.findById(savedEntity.getId()))
      .expectNextMatches(foundEntity ->
        foundEntity.getVersion() == 1
        && foundEntity.getName().equals("n2"))
      .verifyComplete();
  }

  @Test
  void delete() {
	  StepVerifier.create(repository.delete(savedEntity)).verifyComplete();
	    StepVerifier.create(repository.existsById(savedEntity.getId())).expectNext(false).verifyComplete();
  }

  @Test
  void getByProductId() {
	  StepVerifier.create(repository.findByOldLanguageId(savedEntity.getOldLanguageId()))
      .expectNextMatches(foundEntity -> areProductEqual(savedEntity, foundEntity))
      .verifyComplete();
  }

  @Test
  void duplicateError() {
	    OldLanguageEntity entity = new OldLanguageEntity(savedEntity.getOldLanguageId(), "n", 1);
	    StepVerifier.create(repository.save(entity)).expectError(DuplicateKeyException.class).verify();
  }

  @Test
  void optimisticLockError() {

    // Store the saved entity in two separate entity objects
    OldLanguageEntity entity1 = repository.findById(savedEntity.getId()).block();
    OldLanguageEntity entity2 = repository.findById(savedEntity.getId()).block();

    // Update the entity using the first entity object
    entity1.setName("n1");
    repository.save(entity1).block();

    // Update the entity using the second entity object.
    // This should fail since the second entity now holds an old version number, i.e. an Optimistic Lock Error
    StepVerifier.create(repository.save(entity2)).expectError(OptimisticLockingFailureException.class).verify();

    // Get the updated entity from the database and verify its new sate
    StepVerifier.create(repository.findById(savedEntity.getId()))
      .expectNextMatches(foundEntity ->
        foundEntity.getVersion() == 1
        && foundEntity.getName().equals("n1"))
      .verifyComplete();
  }

  private boolean areProductEqual(OldLanguageEntity expectedEntity, OldLanguageEntity actualEntity) {
	    return
	      (expectedEntity.getId().equals(actualEntity.getId()))
	      && (expectedEntity.getVersion() == actualEntity.getVersion())
	      && (expectedEntity.getOldLanguageId() == actualEntity.getOldLanguageId())
	      && (expectedEntity.getName().equals(actualEntity.getName()))
	      && (expectedEntity.getWeight() == actualEntity.getWeight());
	  }
}