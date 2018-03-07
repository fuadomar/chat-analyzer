package tone.analyzer.config;

import javax.annotation.Resource;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tone.analyzer.redis.MessagePublisher;
import tone.analyzer.redis.RedisMessagePublisher;
import tone.analyzer.redis.service.RedisMessageSubscriber;

/**
 * Created by Dell on 1/29/2018.
 */
@Configuration
public class RedisConfig {

  @Resource
  Environment environment;

  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    return new JedisConnectionFactory();
  }

  @Bean
  public StringRedisSerializer stringRedisSerializer() {
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    return stringRedisSerializer;
  }


  @Bean
  public <String, V> RedisTemplate<String, V> genericDTORedisTemplate() {

    RedisTemplate<String, V> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(jedisConnectionFactory());
    redisTemplate.setKeySerializer(stringRedisSerializer());
    redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    return redisTemplate;
  }

  @Bean
  ChannelTopic topic() {
    return new ChannelTopic(environment.getProperty("spring.data.redis.queue"));
  }
}
