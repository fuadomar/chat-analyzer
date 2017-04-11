package tone.analyzer.service;

import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneAnalysis;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneCategory;
import com.ibm.watson.developer_cloud.tone_analyzer.v3.model.ToneScore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/** Created by mozammal on 4/11/17. */
@Component
public class ToneAnalyzerServiceImpl implements ToneAnalyzerService {

  private static final Logger log = LoggerFactory.getLogger(ToneAnalyzerServiceImpl.class);

  @Value("${watson.user.name}")
  private String userName;

  @Value("${watson.user.password}")
  private String password;

  @Override
  public void analyzeMessageTone(final String input) {
    /*
    ToneAnalysis tone = service.getTone(input, null).execute();

    JsonObject json = parser.parse(tone.toString()).getAsJsonObject();*/

  /*  ToneAnalyzerService toneService =
        new ToneAnalyzerService(ToneAnalyzerService.VERSION_DATE_2016_05_19);
    toneService.setUsernameAndPassword(userName, password);
    toneService.setEndPoint("https://gateway.watsonplatform.net/tone-analyzer/api");
    toneService
        .getTone(input, null)
        .enqueue(
            new ServiceCallback<ToneAnalysis>() {
              @Override
              public void onResponse(ToneAnalysis toneAnalysis) {

                for (ToneCategory toneCategory : toneAnalysis.getDocumentTone().getTones()) {
                  for (ToneScore toneScore : toneCategory.getTones()) {
                    log.info("{} {} ", toneScore.getName(), toneScore.getScore());
                  }
                }
              }

              @Override
              public void onFailure(Exception e) {}
            });*/
  }
}
