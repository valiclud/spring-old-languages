package com.github.valiclud.old_languages.core.recommendation.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.github.valiclud.api.composite.core.recommendation.Recommendation;
import com.github.valiclud.api.composite.core.recommendation.RecommendationService;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.old_languages.core.recommendation.persistence.RecommendationEntity;
import com.github.valiclud.old_languages.core.recommendation.persistence.RecommendationRepository;
import com.github.valiclud.util.http.ServiceUtil;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

  private final RecommendationRepository repository;

  private final RecommendationMapper mapper;

  private final ServiceUtil serviceUtil;

  public RecommendationServiceImpl(RecommendationRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public List<Recommendation> getRecommendations(int productId) {

	    if (productId < 1) {
	      throw new InvalidInputException("Invalid productId: " + productId);
	    }
	    
	    List<RecommendationEntity> entityList = repository.findByProductId(productId);
	    List<Recommendation> list = mapper.entityListToApiList(entityList);
	    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

	    LOG.debug("getRecommendations: response size: {}", list.size());

	    return list;
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {
	  try {
	      RecommendationEntity entity = mapper.apiToEntity(body);
	      RecommendationEntity newEntity = repository.save(entity);

	      LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getProductId(), body.getRecommendationId());
	      return mapper.entityToApi(newEntity);

	    } catch (DuplicateKeyException dke) {
	      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Recommendation Id:" + body.getRecommendationId());
	    }
  }

  @Override
  public void deleteRecommendations(int productId) {
	  LOG.debug("deleteRecommendations: tries to delete recommendations for the product with productId: {}", productId);
	    repository.deleteAll(repository.findByProductId(productId));
	  }
}