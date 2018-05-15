package chat.analyzer.redis;

import java.util.Collection;
import java.util.List;

/** Created by Dell on 1/29/2018. */
public interface IHashRepository<V> {

  void put(V obj);

  void multiPut(Collection<V> keys);

  V get(Long id);

  List<V> multiGet(Collection<Long> keys);

  void delete(V key);

  List<V> getObjects();

  void delete();
}
