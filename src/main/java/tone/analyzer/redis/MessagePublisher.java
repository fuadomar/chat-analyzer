package tone.analyzer.redis;

/**
 * Created by Dell on 1/29/2018.
 */
public interface MessagePublisher {

    void publish(String message);
}
