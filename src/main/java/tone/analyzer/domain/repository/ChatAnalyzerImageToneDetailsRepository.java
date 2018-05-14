package tone.analyzer.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tone.analyzer.domain.entity.ChatAnalyzerImageToneDetails;

/** Created by user on 1/28/2018. */
public interface ChatAnalyzerImageToneDetailsRepository
    extends MongoRepository<ChatAnalyzerImageToneDetails, String> {

  public ChatAnalyzerImageToneDetails findByDocumentMetaDataName(String name);
}
