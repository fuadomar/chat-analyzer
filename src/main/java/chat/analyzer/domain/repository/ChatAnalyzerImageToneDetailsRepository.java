package chat.analyzer.domain.repository;

import chat.analyzer.domain.entity.ChatAnalyzerImageToneDetails;
import org.springframework.data.mongodb.repository.MongoRepository;

/** Created by user on 1/28/2018. */
public interface ChatAnalyzerImageToneDetailsRepository
    extends MongoRepository<ChatAnalyzerImageToneDetails, String> {

  public ChatAnalyzerImageToneDetails findByDocumentMetaDataName(String name);
}
