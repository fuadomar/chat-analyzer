package tone.analyzer.redis.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import tone.analyzer.domain.DTO.AwaitingMessagesNotificationDetailsDTO;

/**
 * Created by Dell on 1/29/2018.
 */
@Service
public class RedisNotificationStorageService {

    @Autowired
    RedisTemplate<String, AwaitingMessagesNotificationDetailsDTO> genericDTORedisTemplate;

    public AwaitingMessagesNotificationDetailsDTO findCachedUserAwaitingMessagesNotifications(
            String key) {

        Set<AwaitingMessagesNotificationDetailsDTO> cachedAggregateUserAwaitingMessagesNotifications =
                genericDTORedisTemplate.opsForSet().members(key);

        if (cachedAggregateUserAwaitingMessagesNotifications != null
                && cachedAggregateUserAwaitingMessagesNotifications.size() > 0) {
            for (AwaitingMessagesNotificationDetailsDTO userAwaitingMessageNotifcation :
                    cachedAggregateUserAwaitingMessagesNotifications) {
                return userAwaitingMessageNotifcation;
            }
        }
        return null;
    }

    public AwaitingMessagesNotificationDetailsDTO cacheUserAwaitingMessagesNotification(
            String key, AwaitingMessagesNotificationDetailsDTO notificationDetailsDTO) {

        AwaitingMessagesNotificationDetailsDTO cachedUserAwaitingMessagesNotifications =
                findCachedUserAwaitingMessagesNotifications(key);
        if (cachedUserAwaitingMessagesNotifications != null)
            cachedUserAwaitingMessagesNotifications
                    .getSender()
                    .addAll(notificationDetailsDTO.getSender());
        else {
            cachedUserAwaitingMessagesNotifications =
                    new AwaitingMessagesNotificationDetailsDTO(
                            notificationDetailsDTO.getReceiver(), notificationDetailsDTO.getSender());
        }
        genericDTORedisTemplate.delete(key);
        genericDTORedisTemplate.opsForSet().add(key, cachedUserAwaitingMessagesNotifications);

        return cachedUserAwaitingMessagesNotifications;
    }

    public void deleteAwaitingMessageNotificationByUser(
            String key) {

        genericDTORedisTemplate.delete(key);

    }
}
