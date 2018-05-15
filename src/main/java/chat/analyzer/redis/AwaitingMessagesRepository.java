package chat.analyzer.redis;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import chat.analyzer.domain.DTO.AwaitingChatMessageNotificationDetailsDTO;

/** Created by Dell on 1/29/2018. */
public class AwaitingMessagesRepository
    implements IHashRepository<AwaitingChatMessageNotificationDetailsDTO> {

  @Autowired
  RedisTemplate<String, AwaitingChatMessageNotificationDetailsDTO> genericDTORedisTemplate;

  @Override
  public void put(AwaitingChatMessageNotificationDetailsDTO obj) {}

  @Override
  public void multiPut(Collection<AwaitingChatMessageNotificationDetailsDTO> keys) {}

  @Override
  public AwaitingChatMessageNotificationDetailsDTO get(Long id) {
    return null;
  }

  @Override
  public List<AwaitingChatMessageNotificationDetailsDTO> multiGet(Collection<Long> keys) {
    return null;
  }

  @Override
  public void delete(AwaitingChatMessageNotificationDetailsDTO key) {}

  @Override
  public List<AwaitingChatMessageNotificationDetailsDTO> getObjects() {
    return null;
  }

  @Override
  public void delete() {}
}
