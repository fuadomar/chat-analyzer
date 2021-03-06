package chat.analyzer.redis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

/** Created by Dell on 1/29/2018. */
@Service
public class RedisMessageSubscriber implements MessageListener {

  public static List<String> messageList = new ArrayList<String>();

  public void onMessage(Message message, byte[] pattern) {

    messageList.add(message.toString());
    System.out.println("ChatMessageDTO received: " + message.toString());
  }
}
