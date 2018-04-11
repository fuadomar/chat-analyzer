package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.DocumentMetaData;
import tone.analyzer.domain.entity.ToneAnalyzerChartImageDetails;
import tone.analyzer.domain.model.Document;

/**
 * Created by user on 1/28/2018.
 */
public interface ToneAnalyzerChartImageDetailsRepository extends
        MongoRepository<ToneAnalyzerChartImageDetails, String> {

    public ToneAnalyzerChartImageDetails findByDocumentMetaDataName(String name);
}
