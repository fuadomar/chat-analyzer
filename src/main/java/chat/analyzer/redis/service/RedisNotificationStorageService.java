package chat.analyzer.redis.service;

import chat.analyzer.domain.DTO.UserOnlinePresenceDTO;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import chat.analyzer.domain.DTO.AwaitingChatMessageNotificationDetailsDTO;

/** Created by Dell on 1/29/2018. */
@Service
public class RedisNotificationStorageService {

  @Autowired
  RedisTemplate<String, AwaitingChatMessageNotificationDetailsDTO> genericDTORedisTemplate;

  public AwaitingChatMessageNotificationDetailsDTO findCachedUserAwaitingMessagesNotifications(
      String key) {

    Set<AwaitingChatMessageNotificationDetailsDTO>
        cachedAggregateUserAwaitingMessagesNotifications =
            genericDTORedisTemplate.opsForSet().members(key);

    if (cachedAggregateUserAwaitingMessagesNotifications != null) {
      for (AwaitingChatMessageNotificationDetailsDTO userAwaitingMessageNotifcation :
          cachedAggregateUserAwaitingMessagesNotifications) {
        return userAwaitingMessageNotifcation;
      }
    }
    return null;
  }

  public AwaitingChatMessageNotificationDetailsDTO cacheUserAwaitingMessagesNotification(
      String key, AwaitingChatMessageNotificationDetailsDTO notificationDetailsDTO) {

    AwaitingChatMessageNotificationDetailsDTO cachedUserAwaitingMessagesNotifications =
        findCachedUserAwaitingMessagesNotifications(key);
    if (cachedUserAwaitingMessagesNotifications != null) {
      cachedUserAwaitingMessagesNotifications
          .getSender()
          .addAll(notificationDetailsDTO.getSender());
    } else {
      cachedUserAwaitingMessagesNotifications =
          new AwaitingChatMessageNotificationDetailsDTO(
              notificationDetailsDTO.getReceiver(), notificationDetailsDTO.getSender());
    }
    genericDTORedisTemplate.delete(key);
    genericDTORedisTemplate.opsForSet().add(key, cachedUserAwaitingMessagesNotifications);

    return cachedUserAwaitingMessagesNotifications;
  }

  public void deleteAwaitingMessageNotificationByUser(
      String key, UserOnlinePresenceDTO userOnlinePresenceDTO) {

    if (userOnlinePresenceDTO == null) {
      genericDTORedisTemplate.delete(key);
    } else {
      AwaitingChatMessageNotificationDetailsDTO cachedUserAwaitingMessagesNotifications =
          findCachedUserAwaitingMessagesNotifications(key);
      if (cachedUserAwaitingMessagesNotifications != null
          && cachedUserAwaitingMessagesNotifications.getSender().contains(userOnlinePresenceDTO)) {
        cachedUserAwaitingMessagesNotifications.getSender().remove(userOnlinePresenceDTO);
        genericDTORedisTemplate.delete(key);
        if (cachedUserAwaitingMessagesNotifications.getSender().size() > 0) {
          genericDTORedisTemplate.opsForSet().add(key, cachedUserAwaitingMessagesNotifications);
        }
      }
    }
  }
}
