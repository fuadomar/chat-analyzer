package tone.analyzer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import tone.analyzer.domain.entity.Review;
import tone.analyzer.gateway.ReviewGateway;

/**
 * Created by mozammal on 4/25/17.
 */
@RestController
public class ReviewController {

  private static final Logger log = LoggerFactory.getLogger(ReviewController.class);

  @Autowired
  private ReviewGateway reviewGateway;

  /*@PreAuthorize("hasRole('ROLE_USER')")*/
  @RequestMapping(value = "/review", method = RequestMethod.POST, consumes = "application/json")
  public ResponseEntity<String> sendChatMessageToDestination(@RequestBody Review review) {
    reviewGateway.saveReview(review);
    String success = "Success";
    return new ResponseEntity<>(success, new HttpHeaders(), HttpStatus.CREATED);
  }
}
