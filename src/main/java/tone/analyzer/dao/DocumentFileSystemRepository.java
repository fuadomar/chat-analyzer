package tone.analyzer.dao;

import java.io.IOException;
import tone.analyzer.domain.model.Document;

/**
 * Created by Dell on 1/17/2018.
 */
public interface DocumentFileSystemRepository {

  public void add(Document document) throws IOException;
}
