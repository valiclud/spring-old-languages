package com.github.valiclud.api.composite.core.review;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

public interface ReviewService {

	/**
	 * Sample usage: "curl $HOST:$PORT/review?productId=1".
	 *
	 * @param productId Id of the product
	 * @return the reviews of the product
	 */
	@GetMapping(value = "/review/{reviewId}", produces = "application/json")
	List<Review> getReviews(@PathVariable("reviewId") int productId);
}