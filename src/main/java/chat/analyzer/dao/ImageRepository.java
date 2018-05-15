package chat.analyzer.dao;

import chat.analyzer.domain.model.Document;
import java.io.IOException;

/** Created by Dell on 1/17/2018. */
public interface ImageRepository {

  public void add(Document document, boolean isBAse64Image) throws IOException;
}
