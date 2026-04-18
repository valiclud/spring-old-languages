package com.github.valiclud.old_languages.core.ol.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import com.github.valiclud.api.composite.core.ol.OldLanguage;
import com.github.valiclud.api.composite.core.ol.OldLanguageService;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.api.exceptions.NotFoundException;
import com.github.valiclud.util.http.ServiceUtil;

@RestController
public class OldLanguageServiceImpl implements OldLanguageService {

	private static final Logger LOG = LoggerFactory.getLogger(OldLanguageServiceImpl.class);

	private final ServiceUtil serviceUtil;

	public OldLanguageServiceImpl(ServiceUtil serviceUtil) {
		this.serviceUtil = serviceUtil;
	}

	@Override
	public OldLanguage getOldLanguage(Long oldLanguageId) {
		LOG.debug("/product return the found product for oldlaguageId={}", oldLanguageId);

		if (oldLanguageId < 1) {
			throw new InvalidInputException("Invalid productId: " + oldLanguageId);
		}

		if (oldLanguageId == 13) {
			throw new NotFoundException("No product found for productId: " + oldLanguageId);
		}

		return new OldLanguage(oldLanguageId, "name-" + oldLanguageId, 123, serviceUtil.getServiceAddress());

	}
}