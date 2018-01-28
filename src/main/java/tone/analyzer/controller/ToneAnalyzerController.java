package tone.analyzer.controller;

import io.indico.api.utils.IndicoException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.DTO.*;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.gateway.ToneAnalyzerGateway;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Created by mozammal on 4/11/17. */
@RestController
public class ToneAnalyzerController {

  @Autowired private ToneAnalyzerGateway toneAnalyzerGateway;

  private static final Logger LOG = LoggerFactory.getLogger(ToneAnalyzerController.class);

  public String getURLBase(HttpServletRequest request) throws MalformedURLException {

    URL requestURL = new URL(request.getRequestURL().toString());
    String port = requestURL.getPort() == -1 ? "" : ":" + requestURL.getPort();
    return requestURL.getProtocol() + "://" + requestURL.getHost() + port;
  }

  private File getFileFromURL() {
    URL url = getClass().getClassLoader().getResource("upload-images");
    File file = null;
    try {
      file = new File(url.toURI());
    } catch (URISyntaxException e) {
      file = new File(url.getPath());
    } finally {
      return file;
    }
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/upload/images", method = RequestMethod.POST)
  public @ResponseBody String uploadImage(
      @RequestParam("image") String image, HttpServletRequest request) {
    try {

      String token = UUID.randomUUID().toString();
      LOG.info("image base64:  {}", image);
      String delimiter = "data:image/png;base64,";
      int imageLength = image.length();
      String bse64Image = image.substring(delimiter.length(), imageLength - 2);
      String s = image.substring(imageLength - 2);
      HttpClient httpclient = HttpClients.createDefault();
      HttpPost httppost = new HttpPost("http://data-uri-to-img-url.herokuapp.com/images.json");

      List<NameValuePair> params = new ArrayList<NameValuePair>(1);
      params.add(new BasicNameValuePair("image[data_uri]", bse64Image));
      httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

      HttpResponse response = httpclient.execute(httppost);
      HttpEntity entity = response.getEntity();
      String responseString = EntityUtils.toString(entity, "UTF-8");
      return responseString;

    } catch (Exception e) {
      return "error = " + e;
    }
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-people-individual", method = RequestMethod.GET)
  public PeopleDTO analyzeStatedPeopleTone(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeStatedPeopleTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-places-individual", method = RequestMethod.GET)
  public PlacesDTO analyzeStatedPlacesTone(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeStatedPlacesTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-organizations-individual", method = RequestMethod.GET)
  public OrganizationsDTO analyzeStatedOrganizationsTone(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeStatedOrganizationsTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/aspect-analyzer-individual", method = RequestMethod.GET)
  public String analyzeIndividualAspect(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeIndividualAspect(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/texttag-analyzer-individual", method = RequestMethod.GET)
  public TextTagDTO analyzeIndividualContext(@RequestParam("sender") String sender)
      throws IOException, IndicoException, URISyntaxException {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzeIndividualTextTag(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_USER')")
  @RequestMapping(value = "/tone-analyzer-between-users", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerConversationalTone(
      @RequestParam("sender") String sender, @RequestParam("recipient") String recipient) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    chatMessage.setRecipient(recipient);
    return toneAnalyzerGateway.analyzerConversationalTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/tone-analyzer-individual", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzerIndividualConversationalTone(
      @RequestParam("sender") String sender) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(sender);
    return toneAnalyzerGateway.analyzerIndividualConversationalTone(chatMessage);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @RequestMapping(value = "/review-analyzer", method = RequestMethod.GET)
  public ToneAnalyzerFeedBackDTO analyzeReviewTone(@RequestParam("reviewer") String reviewer) {

    ChatMessage chatMessage = new ChatMessage();
    chatMessage.setSender(reviewer);
    return toneAnalyzerGateway.analyzeReviewTone(chatMessage);
  }
}
