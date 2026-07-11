package com.github.valiclud.old_languages.core.ol.services;

import static java.util.logging.Level.FINE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.api.composite.core.ol.OldLanguageService;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.api.exceptions.NotFoundException;
import com.github.valiclud.old_languages.core.ol.persistence.OldLanguageEntity;
import com.github.valiclud.old_languages.core.ol.persistence.OldLanguageRepository;
import com.github.valiclud.util.http.ServiceUtil;

@RestController
public class OldLanguageServiceImpl implements OldLanguageService {

	private static final Logger LOG = LoggerFactory.getLogger(OldLanguageServiceImpl.class);

	private final ServiceUtil serviceUtil;

	private final OldLanguageRepository repository;

	private final OldLanguageMapper mapper;

	public OldLanguageServiceImpl(OldLanguageRepository repository, OldLanguageMapper mapper, ServiceUtil serviceUtil) {
		this.repository = repository;
		this.mapper = mapper;
		this.serviceUtil = serviceUtil;
	}


	@Override
	public Mono<OldLanguage> getOldLanguage(int oldLanguageId) {
		if (oldLanguageId < 1) {
			throw new InvalidInputException("Invalid productId: " + oldLanguageId);
		}

		LOG.info("Will get oldLanguage info for id={}", oldLanguageId);
		
		return repository.findByOldLanguageId(oldLanguageId)
			      .switchIfEmpty(Mono.error(new NotFoundException("No oldLanguage found for oldLanguageId: " + oldLanguageId)))
			      .log(LOG.getName(), FINE)
			      .map(e -> mapper.entityToApi(e))
			      .map(e -> setServiceAddress(e));
	}

	@Override
	public Mono<OldLanguage> createOldLanguage(OldLanguage body) {
		
		if (body.getOldLanguageId() < 1) {
		      throw new InvalidInputException("Invalid oldLanguageId: " + body.getOldLanguageId());
		    }
		OldLanguageEntity entity = mapper.apiToEntity(body);
		Mono<OldLanguage> newEntity = repository.save(entity)
		  .log(LOG.getName(), FINE)
	      .onErrorMap(
	        DuplicateKeyException.class,
	        ex -> new InvalidInputException("Duplicate key, OldLanguage Id: " + body.getOldLanguageId()))
	      .map(e -> mapper.entityToApi(e));
		
		return newEntity;
	}

	@Override
	public Mono<Void> deleteOldLanguage(int oldLanguageId) {
		if (oldLanguageId < 1) {
			throw new InvalidInputException("Invalid productId: " + oldLanguageId);
		}
		
		LOG.debug("deleteProduct: tries to delete an entity with productId: {}", oldLanguageId);
		 return repository.findByOldLanguageId(oldLanguageId).log(LOG.getName(), FINE).map(e -> repository.delete(e)).flatMap(e -> e);
	}
	
	 private OldLanguage setServiceAddress(OldLanguage e) {
		    e.setServiceAddress(serviceUtil.getServiceAddress());
		    return e;
		  }
}