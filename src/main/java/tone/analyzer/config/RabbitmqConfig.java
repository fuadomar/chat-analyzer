package tone.analyzer.config;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import tone.analyzer.listener.RabbitmqMessageListener;

import javax.annotation.Resource;

/**
 * Created by Dell on 1/15/2018.
 */
@Configuration
@PropertySource("classpath:application.properties")
public class RabbitmqConfig {

    @Resource
    private Environment environment;

    @Bean
    Queue queue() {
        return new Queue(environment.getProperty("spring.rabbitmq.queue"), false);
    }

    @Bean
    String rabbitmqQueue() {
        return new String(environment.getProperty("spring.rabbitmq.queue"));
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(environment.getProperty("spring.rabbitmq.exchange"));
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(environment.getProperty("spring.rabbitmq.queue"));
    }

    @Bean
    public MessageConverter messageConverter() {
        final Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        return converter;
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(environment.getProperty("spring.rabbitmq.queue"));
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory adapterOPListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean
    RabbitmqMessageListener receiver() {
        return new RabbitmqMessageListener();
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RabbitmqMessageListener receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
