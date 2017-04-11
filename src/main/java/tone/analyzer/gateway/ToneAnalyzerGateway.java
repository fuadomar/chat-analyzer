package tone.analyzer.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tone.analyzer.service.ToneAnalyzerService;

/**
 * Created by mozammal on 4/11/17.
 */

@Component
public class ToneAnalyzerGateway {

    @Autowired
    private ToneAnalyzerService toneAnalyzerService;

    public void analyzerMessageTone() {

    }
}
