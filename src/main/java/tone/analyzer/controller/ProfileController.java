package tone.analyzer.controller;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Dell on 1/16/2018.
 */

@RestController
public class ProfileController {

  @RequestMapping(value = "/upload", method = RequestMethod.POST, consumes = {"multipart/form-data"})
  public ResponseEntity<?> upload(@RequestPart(value = "file", required = true) MultipartFile file) throws IOException {

    if (!file.isEmpty()) {

      return new ResponseEntity<>(new HttpHeaders(), HttpStatus.CREATED);
    }
    return new ResponseEntity<>(new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

}
