package com.github.valiclud.old_languages.core.review.services;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.github.valiclud.api.composite.core.review.Review;
import com.github.valiclud.api.composite.core.review.ReviewService;
import com.github.valiclud.api.exceptions.InvalidInputException;
import com.github.valiclud.old_languages.core.review.persistence.ReviewEntity;
import com.github.valiclud.old_languages.core.review.persistence.ReviewRepository;
import com.github.valiclud.util.http.ServiceUtil;
import com.github.valiclud.old_languages.core.review.services.ReviewMapper;

@RestController
public class ReviewServiceImpl implements ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

  private final ReviewRepository repository;
  
  private final ReviewMapper reviewMapper;

  private final ServiceUtil serviceUtil;

  public ReviewServiceImpl(ReviewRepository repository, ReviewMapper reviewMapper, ServiceUtil serviceUtil) {
    this.repository = repository;
    this.reviewMapper = reviewMapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Review createReview(Review body) {
    try {
      ReviewEntity entity = reviewMapper.apiToEntity(body);
      ReviewEntity newEntity = repository.save(entity);

      LOG.debug("createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return reviewMapper.entityToApi(newEntity);

    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException("Duplicate key, Product Id: " + body.getProductId() + ", Review Id:" + body.getReviewId());
    }
  }

  @Override
  public List<Review> getReviews(int productId) {

    if (productId < 1) {
      throw new InvalidInputException("Invalid productId: " + productId);
    }
    
    List<ReviewEntity> entityList = repository.findByProductId(productId);
    List<Review> list = this.reviewMapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("getReviews: response size: {}", list.size());

    return list;
  }

  @Override
  public void deleteReviews(int productId) {
    LOG.debug("deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
  
}