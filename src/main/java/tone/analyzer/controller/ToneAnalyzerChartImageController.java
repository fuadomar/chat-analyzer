package tone.analyzer.controller;

import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import tone.analyzer.dao.ImageRepository;
import tone.analyzer.domain.entity.DocumentMetaData;
import tone.analyzer.domain.entity.ToneAnalyzerChartImageDetails;
import tone.analyzer.domain.model.Document;
import tone.analyzer.domain.repository.ToneAnalyzerChartImageDetailsRepository;
import tone.analyzer.gateway.ProfileImageGateway;
import tone.analyzer.utility.ToneAnalyzerUtility;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.Date;
import java.util.UUID;

/**
 * Created by user on 1/28/2018.
 */

@RestController
public class ToneAnalyzerChartImageController {

    private static final Logger LOG = LoggerFactory.getLogger(ToneAnalyzerChartImageController.class);

    @Autowired
    private ToneAnalyzerUtility toneAnalyzerUtility;

    @Value("${tone.analyzer.image.repository")
    private String toneAnalyzerImageStorageLocation;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ToneAnalyzerChartImageDetailsRepository toneAnalyzerChartImageDetailsRepository;

    @Autowired
    private ProfileImageGateway profileImageGateway;

    @RequestMapping(value = "/tone_analyzer/images/{image}", method = RequestMethod.GET)
    public void retrieveImageAsByteArray(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable("image") String image,
            Principal principal)
            throws IOException {

        ToneAnalyzerChartImageDetails toneAnalyzerChartImageDetails = toneAnalyzerChartImageDetailsRepository.findByDocumentMetaDataName(image);
        if (toneAnalyzerChartImageDetails == null || toneAnalyzerChartImageDetails.getDocumentMetaData() == null)
            return;
        DocumentMetaData documentMetaData = toneAnalyzerChartImageDetails.getDocumentMetaData();
        if (documentMetaData != null && !StringUtils.isEmpty(documentMetaData.getName()))
            profileImageGateway.retrieveImageAsByteArray(request, response, image, true);
    }


    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/tone_analyzer/upload/images", method = RequestMethod.POST)
    public
    @ResponseBody
    String uploadBase64Image(
            @RequestParam("image") String image, HttpServletRequest request, Principal principal) {
        try {

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String loggedInUser;
            if (auth instanceof OAuth2Authentication)
                loggedInUser = new ToneAnalyzerUtility().findPrincipalNameFromAuthentication(auth);
            else loggedInUser = auth.getName();

            String loggedInUserSignature = loggedInUser + System.currentTimeMillis();
            String sha256hex =
                    Hashing.sha256().hashString(loggedInUserSignature, StandardCharsets.UTF_8).toString();
            String imageName = sha256hex + UUID.randomUUID().toString() + ".png";

            LOG.info("image base64:  {}", image);
            String delimiter = "data:image/png;base64,";
            int imageLength = image.length();
            String base64Image = image.substring(delimiter.length(), imageLength - 2);

            byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);

            BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));

            File outputImageFile = new File(imageName);
            ImageIO.write(img, "png", outputImageFile);

            Document document = new Document(imageName, imageBytes);
            DocumentMetaData documentMetaData =
                    new DocumentMetaData(imageName, toneAnalyzerImageStorageLocation, new Date());
            imageRepository.add(document, true);
            toneAnalyzerChartImageDetailsRepository.save(new ToneAnalyzerChartImageDetails(loggedInUser, documentMetaData));

           /* String s = image.substring(imageLength - 2);
            HttpClient httpclient = HttpClients.createDefault();
            HttpPost httppost = new HttpPost("http://data-uri-to-img-url.herokuapp.com/images.json");

            List<NameValuePair> params = new ArrayList<NameValuePair>(1);
            params.add(new BasicNameValuePair("image[data_uri]", base64Image));
            httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");*/
            return toneAnalyzerUtility.retrieveRootHostUrl(request) + "/tone_analyzer/images/" + document.getName();

        } catch (Exception e) {
            return "error = " + e;
        }
    }
}
