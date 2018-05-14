package tone.analyzer.dao;

import java.io.IOException;

import tone.analyzer.domain.model.Document;

/** Created by Dell on 1/17/2018. */
public interface ImageRepository {

  public void add(Document document, boolean isBAse64Image) throws IOException;
}
