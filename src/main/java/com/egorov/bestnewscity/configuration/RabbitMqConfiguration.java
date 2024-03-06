package com.egorov.bestnewscity.configuration;


import com.egorov.bestnewscity.model.entity.CategoryNews;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableRabbit
public class RabbitMqConfiguration {

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }


    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange("direct-news");
    }

    @Bean
    public Declarables createAllQueue() {
        return new Declarables(createQueueForNews());
    }


    @Bean
    public Declarables createBinding() {
        List<Binding> bindings = createQueueForNews()
                .stream()
                .map(queue ->
                     BindingBuilder.bind(queue)
                            .to(directExchange())
                            .with(queue.getName().
                                    replace("queue-news-", "").trim().toUpperCase())).toList();

        return new Declarables(bindings);
    }

    private List<Queue> createQueueForNews() {
        CategoryNews[] categoryNews = CategoryNews.values();
        List<Queue> queues = new ArrayList<>();
        for (CategoryNews category : categoryNews) {
            Queue queue = new Queue("queue-news-" + category.name().toLowerCase());
            queues.add(queue);
        }
        return queues;
    }

}
