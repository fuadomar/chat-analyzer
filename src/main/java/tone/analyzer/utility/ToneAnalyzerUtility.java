package tone.analyzer.utility;

import com.ibm.watson.developer_cloud.tone_analyzer.v3.ToneAnalyzer;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;
import io.indico.Indico;
import io.indico.api.results.IndicoResult;
import io.indico.api.text.TextTag;
import io.indico.api.utils.IndicoException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Component;
import tone.analyzer.domain.DTO.*;
import tone.analyzer.domain.entity.*;
import tone.analyzer.domain.model.ChatMessage;
import tone.analyzer.domain.repository.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by mozammal on 4/26/17.
 */
@Component
public class ToneAnalyzerUtility {

  /* attributes returned from watson

  Anger 0.159056
  Disgust 0.143912
  Fear 0.405248
  Joy 0.047352
  Sadness 0.301982
  Analytical 0.737339
  Confident 0.0
  Tentative 0.0
  Openness 0.625828
  Conscientiousness 0.418775
  Extraversion 0.687993
  Agreeableness 0.205669
  Emotional Range 0.851521*/

    /**
     * supported aspects domain
     * <p>
     * <p>
     * <p>
     * <p>"hotels" "restaurants" "cars" "airlines" *
     */
    private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerUtility.class);

    private static final String LANGUAGE_ENGLISH = "en";

    private static final String KEY_FOR_MEANINGCLOUD = "key";

    private static final String LANG_FOR_MEANINGCLOUD = "lang";

    private static final String TXT_FOR_MEANINGCLOUD = "txt";

    private final String X_AYLIEN_TEXT_API_APPLICATION_ID = "X-AYLIEN-TextAPI-Application-ID";

    private final String X_AYLIEN_TEXT_API_APPLICATION_KEY = "X-AYLIEN-TextAPI-Application-Key";

    private double ACCEPTED_WEIGHTED_THRESHOLD = 0.5;

    @Value("${aylien.application.id}")
    private String aylieaApplicationId;

    @Value("${aylien.application.key}")
    private String aylienApplicationKey;

    @Value("${watson.user.name}")
    private String userName;

    @Value("${watson.user.password}")
    private String password;

    @Value("${indico.api.key}")
    private String indicoApiKey;

    @Value("${meaningcloud.license.key}")
    private String meaningcloudLicenceKey;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private FlaggedMessageRepository flaggedMessageRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserAccountRepository accountRepository;


    public String findPrincipalNameFromAuthentication(org.springframework.security.core.Authentication authentication) {

        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication userPrincipal = (OAuth2Authentication) authentication;
            org.springframework.security.core.Authentication authentication1 = userPrincipal.getUserAuthentication();
            Map<String, String> details = null;

            if (authentication1.getDetails() instanceof Map<?, ?>) {
                Object emails = ((Map<String, String>) authentication1.getDetails()).get("emails");
                String email = ((List<Map<String, String>>) emails).get(0).get("value");
                return email;
            }
        }

        return authentication.getName();
    }

    public ToneAnalyzerFeedBackDTO analyzeToneBetweenTwoUserByIBMWatson(ChatMessage chatMessage) {

        ToneAnalyzer toneAnalyzer = getIBMToneAnalyzer();
        String msg = fetchAllReceivedMessagesFromSpecificSender(chatMessage);
        if (msg == null) return null;
        log.debug("text: {}", msg);
        List<String> likelyToneInMessage = new ArrayList<>();
        List<Double> likelyToneScore = new ArrayList<>();
        ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO =
                getToneAnalyzerFeedBackDTO(toneAnalyzer, msg, likelyToneInMessage, likelyToneScore);
        if (!likelyToneInMessage.isEmpty()) {
            FlaggedMessage flaggedMessage =
                    createFlaggedMessage(chatMessage, msg, likelyToneInMessage, likelyToneScore);
            flaggedMessageRepository.save(flaggedMessage);
        }
        return toneAnalyzerFeedBackDTO;
    }

    public ToneAnalyzerFeedBackDTO analyzeIndividualToneByIBMWatson(ChatMessage chatMessage) {

        ToneAnalyzer toneAnalyzer = getIBMToneAnalyzer();
        String msg = fetchAllReceivedMessagesFromSpecificSender(chatMessage);
        if (msg == null) return null;

        log.debug("text: {}", msg);
        List<String> likelyToneInMessage = new ArrayList<>();
        List<Double> likelyToneScore = new ArrayList<>();
        ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO =
                getToneAnalyzerFeedBackDTO(toneAnalyzer, msg, likelyToneInMessage, likelyToneScore);
        if (!likelyToneInMessage.isEmpty()) {
            FlaggedMessage flaggedMessage =
                    createFlaggedMessage(chatMessage, msg, likelyToneInMessage, likelyToneScore);
            flaggedMessageRepository.save(flaggedMessage);
        }
        return toneAnalyzerFeedBackDTO;
    }

    private String fetchAllReceivedMessagesFromSpecificSender(ChatMessage chatMessage) {

        Account sender = accountRepository.findOne(chatMessage.getSender());
        if (sender == null) return null;
        Sort sort = new Sort(Sort.Direction.ASC, "createdTime");
        List<Message> messagesBySenderAndReceiver =
                messageRepository.findReceivedMessagesByReceiverFromSender(
                        sender.getName(), chatMessage.getRecipient(), sort);
        String msg = "";
        for (Message message : messagesBySenderAndReceiver) msg = msg + message.getContent() + "\n";
        return msg;
    }

    public ToneAnalyzerFeedBackDTO analyzeReviewToneByIBMWatson(ChatMessage chatMessage) {

        ToneAnalyzer toneAnalyzer = getIBMToneAnalyzer();
        List<Review> reviews = reviewRepository.findByUser(chatMessage.getSender());
        if (reviews == null) return null;
        String reviewedContent = "";
        for (Review review : reviews) reviewedContent = reviewedContent + review.getContent() + "\n";

        log.debug("text: {}", reviewedContent);
        List<String> likelyToneInMessage = new ArrayList<>();
        List<Double> likelyToneScore = new ArrayList<>();
        ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO =
                getToneAnalyzerFeedBackDTO(
                        toneAnalyzer, reviewedContent, likelyToneInMessage, likelyToneScore);

        return toneAnalyzerFeedBackDTO;
    }

    private ToneAnalyzerFeedBackDTO getToneAnalyzerFeedBackDTO(
            ToneAnalyzer toneAnalyzer,
            String msg,
            List<String> likelyToneInMessage,
            List<Double> likelyToneScore) {
        ToneAnalyzerFeedBackDTO toneAnalyzerFeedBackDTO = new ToneAnalyzerFeedBackDTO();
        ToneAnalysis tone = toneAnalyzer.getTone(msg, null).execute();

        for (ToneCategory toneCategory : tone.getDocumentTone().getTones()) {
            for (ToneScore toneScore : toneCategory.getTones()) {
                log.debug("{} {} ", toneScore.getName(), toneScore.getScore());
                if (toneScore.getScore() >= ACCEPTED_WEIGHTED_THRESHOLD) {
                    toneAnalyzerFeedBackDTO.put(toneScore.getName(), toneScore.getScore());
                    likelyToneInMessage.add(toneScore.getName());
                    likelyToneScore.add(toneScore.getScore());
                }
            }
        }
        return toneAnalyzerFeedBackDTO;
    }

    private FlaggedMessage createFlaggedMessage(
            ChatMessage chatMessage,
            String msg,
            List<String> likelyToneInMessage,
            List<Double> likelyToneScore) {
        FlaggedMessage flaggedMessage = new FlaggedMessage();
        flaggedMessage.setContent(msg);
        flaggedMessage.setLikelyTone(likelyToneInMessage);
        flaggedMessage.setSender(chatMessage.getSender());
        flaggedMessage.setLikelyToneScore(likelyToneScore);
        return flaggedMessage;
    }

    private ToneAnalyzer getIBMToneAnalyzer() {
        ToneAnalyzer toneAnalyzer = new ToneAnalyzer(ToneAnalyzer.VERSION_DATE_2016_05_19);
        toneAnalyzer.setUsernameAndPassword(userName, password);
        return toneAnalyzer;
    }

    public TextTagDTO analyzeIndividualTextTag(ChatMessage chatMessage)
            throws IOException, IndicoException, URISyntaxException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("threshold", 0.1);
        Indico indico = new Indico(indicoApiKey);
        String msg = fetchAllReceivedMessagesFromSpecificSender(chatMessage);
        IndicoResult single = indico.textTags.predict(msg, params);
        Map<TextTag, Double> textTagMap = single.getTextTags();
        TextTagDTO textTagDTO = new TextTagDTO();

        for (Map.Entry<TextTag, Double> entry : textTagMap.entrySet()) {
            TextTag key = entry.getKey();
            Double value = entry.getValue();
            String keyName = key.name();
            textTagDTO.put(keyName, value);
        }
        log.info("message: {}", msg);
        log.info("textTag: " + textTagMap);
        return textTagDTO;
    }

    public String analyzeIndividualAspect(ChatMessage chatMessage)
            throws IOException, IndicoException, URISyntaxException {

        String msg = fetchAllReceivedMessagesFromSpecificSender(chatMessage);
        final HttpClient httpClient = HttpClientBuilder.create().build();
        URI uri = buildUriBuilder(msg);
        List<NameValuePair> nameValuePairList = new ArrayList<>();
        nameValuePairList.add(new BasicNameValuePair(KEY_FOR_MEANINGCLOUD, meaningcloudLicenceKey));
        nameValuePairList.add(new BasicNameValuePair(LANG_FOR_MEANINGCLOUD, LANGUAGE_ENGLISH));
        nameValuePairList.add(new BasicNameValuePair(TXT_FOR_MEANINGCLOUD, msg));
        HttpPost httpGet = new HttpPost(uri);
        httpGet.addHeader("content-type", "application/x-www-form-urlencoded");
        httpGet.setEntity(new UrlEncodedFormEntity(nameValuePairList));
        HttpResponse rawResponse = httpClient.execute(httpGet);
        StringBuffer result = retrieveAylienApiResponse(rawResponse);
        log.info("output: " + result);

        return result.toString();
    }

    public OrganizationsDTO analyzeStatedOrganizationsTone(ChatMessage chatMessage)
            throws IOException, IndicoException, URISyntaxException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("threshold", 0.1);
        Indico indico = new Indico(indicoApiKey);
        String msg = fetchAllReceivedMessagesFromSpecificSender(chatMessage);
        IndicoResult single = indico.organizations.predict(msg, params);
        List<Map<String, Object>> organizationsList = single.getOrganizations();
        OrganizationsDTO organizationsDTO = new OrganizationsDTO();
        constructNormalizedDTOFromIndicoResponse(organizationsList, organizationsDTO);
        log.info("organizations: {}", organizationsList);

        return organizationsDTO;
    }

    public PlacesDTO analyzeStatedPlaces(ChatMessage chatMessage)
            throws IOException, IndicoException, URISyntaxException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("threshold", 0.1);
        Indico indico = new Indico(indicoApiKey);
        String msg = fetchAllReceivedMessagesFromSpecificSender(chatMessage);
        IndicoResult single = indico.places.predict(msg, params);
        List<Map<String, Object>> placesList = single.getPlaces();
        PlacesDTO organizationsDTO = new PlacesDTO();
        constructNormalizedDTOFromIndicoResponse(placesList, organizationsDTO);
        log.info("places: {}", placesList);
        return organizationsDTO;
    }

    public PeopleDTO analyzeStatedPeopleTone(ChatMessage chatMessage)
            throws IndicoException, IOException {

        HashMap<String, Object> params = new HashMap<>();
        params.put("threshold", 0.1);
        Indico indico = new Indico(indicoApiKey);
        String msg = fetchAllReceivedMessagesFromSpecificSender(chatMessage);
        IndicoResult single = indico.people.predict(msg, params);
        List<Map<String, Object>> peopleList = single.getPeople();
        PeopleDTO peopleDTO = new PeopleDTO();
        constructNormalizedDTOFromIndicoResponse(peopleList, peopleDTO);
        log.info("people: {}", peopleList);
        return peopleDTO;
    }

    private void constructNormalizedDTOFromIndicoResponse(
            List<Map<String, Object>> mapList, LinkedHashMap<String, Double> linkedDTO) {
        String locationName = "";
        Double probability = 0.0;
        for (Map<String, Object> element : mapList) {
            for (Map.Entry<String, Object> entry : element.entrySet()) {

                String key = entry.getKey();
                if (key.equalsIgnoreCase("position")) {
                    linkedDTO.put(locationName, probability);
                    continue;
                }
                Object value = entry.getValue();
                if (key.equalsIgnoreCase("text")) {
                    locationName = (String) value;
                } else {
                    probability = Double.parseDouble(value + "");
                }
            }
        }
    }

    private URI buildUriBuilder(String msg) throws URISyntaxException {
        // http://api.meaningcloud.com/parser-2.0
        return new URIBuilder()
                .setScheme("http")
                .setHost("api.meaningcloud.com")
                .setPath("/sentiment-2.1")
                .build();
    }

    private StringBuffer retrieveAylienApiResponse(HttpResponse rawResponse) throws IOException {
        BufferedReader rd =
                new BufferedReader(new InputStreamReader(rawResponse.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return result;
    }
}
