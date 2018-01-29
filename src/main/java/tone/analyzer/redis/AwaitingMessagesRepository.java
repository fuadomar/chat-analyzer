package tone.analyzer.redis;

import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import tone.analyzer.domain.DTO.AwaitingMessagesNotificationDetailsDTO;

/** Created by Dell on 1/29/2018. */
public class AwaitingMessagesRepository
    implements IHashRepository<AwaitingMessagesNotificationDetailsDTO> {

  @Autowired RedisTemplate<String, AwaitingMessagesNotificationDetailsDTO> genericDTORedisTemplate;

  @Override
  public void put(AwaitingMessagesNotificationDetailsDTO obj) {}

  @Override
  public void multiPut(Collection<AwaitingMessagesNotificationDetailsDTO> keys) {}

  @Override
  public AwaitingMessagesNotificationDetailsDTO get(Long id) {
    return null;
  }

  @Override
  public List<AwaitingMessagesNotificationDetailsDTO> multiGet(Collection<Long> keys) {
    return null;
  }

  @Override
  public void delete(AwaitingMessagesNotificationDetailsDTO key) {}

  @Override
  public List<AwaitingMessagesNotificationDetailsDTO> getObjects() {
    return null;
  }

  @Override
  public void delete() {}
}
