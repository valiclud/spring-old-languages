package com.github.valiclud.old_languages.core.ol.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;

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
	public OldLanguage getOldLanguage(int oldLanguageId) {
		if (oldLanguageId < 1) {
			throw new InvalidInputException("Invalid productId: " + oldLanguageId);
		}

		OldLanguageEntity entity = repository.findByOldLanguageId(oldLanguageId)
				.orElseThrow(() -> new NotFoundException("No product found for productId: " + oldLanguageId));

		OldLanguage response = mapper.entityToApi(entity);
		response.setServiceAddress(serviceUtil.getServiceAddress());

		LOG.debug("getOldLanguage: found oldLanguageId: {}", response.getOldLanguageId());

		return response;
	}

	@Override
	public OldLanguage createOldLanguage(OldLanguage body) {
		try {
			OldLanguageEntity entity = mapper.apiToEntity(body);
			OldLanguageEntity newEntity = repository.save(entity);

			LOG.debug("createProduct: entity created for productId: {}", body.getOldLanguageId());
			return mapper.entityToApi(newEntity);

		} catch (DuplicateKeyException dke) {
			throw new InvalidInputException("Duplicate key, OldLanguage Id: " + body.getOldLanguageId());
		}
	}

	@Override
	public void deleteOldLanguage(int oldLanguageId) {
		LOG.debug("deleteProduct: tries to delete an entity with productId: {}", oldLanguageId);
	    repository.findByOldLanguageId(oldLanguageId).ifPresent(e -> repository.delete(e));
		
	}
}