package tone.analyzer.controller;

import io.indico.api.utils.IndicoException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.domain.DTO.*;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.gateway.ToneAnalyzerGateway;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by mozammal on 4/11/17.
 */
@RestController
public class ToneAnalyzerController {

    @Autowired
    private ToneAnalyzerGateway toneAnalyzerGateway;

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
    public @ResponseBody String uploadImage(@RequestParam("image") String image, HttpServletRequest request) {
        try {
            String token = UUID.randomUUID().toString();
            String delimiter = "data:image/png;base64,";
            String bse64Image =  image.substring(delimiter.length());
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("http://data-uri-to-img-url.herokuapp.com/images.json");
            //httppost.setHeader("Content-Type", "text; charset=UTF-8");

// Request parameters and other properties.
            List<NameValuePair> params = new ArrayList<NameValuePair>(2);
            params.add(new BasicNameValuePair("image[data_uri]", bse64Image));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
          /*  params.add(new BasicNameValuePair("param-2", "Hello!"));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));*/

//Execute and get the response.
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            return responseString;

          /*  if (entity != null) {
                InputStream instream = entity.getContent();

                try {
                    // do something useful
                } finally {
                    instream.close();
                }
            }*/

          /*  byte[] imageByte = Base64.decodeBase64(image);
            ClassLoader classLoader = getClass().getClassLoader();
            //File file = new File(classLoader.getResource("upload-images").getFile());
            String path = request.getSession().getServletContext().getRealPath("/resources/upload-images");
            //return new FileSystemResource(new File(path));
            String directory = path + "/" + token + ".jpg";
            new FileOutputStream(directory).write(imageByte);
            return getURLBase(request) + "images/sample" + token + ".jpg";*/
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
