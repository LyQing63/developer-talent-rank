package com.talent.config;

import com.talent.constant.RabbitMQConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MQConfig {

    @Bean
    public DirectExchange apiDirectExchange() {
        return new DirectExchange(RabbitMQConstant.API_EXCHANGE_NAME, true, false);
    }

    @Bean
    public DirectExchange aiDirectExchange() {
        return new DirectExchange(RabbitMQConstant.AI_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue apiQueue() {
        return new Queue(RabbitMQConstant.API_QUEUE_NAME, true);
    }

    @Bean
    public Queue aiQueue() {
        return new Queue(RabbitMQConstant.AI_QUEUE_NAME, true);
    }

    @Bean
    public Binding bindingApiQueue() {
        return BindingBuilder.bind(apiQueue())
                .to(apiDirectExchange())
                .with(RabbitMQConstant.API_ROUTING_KEY);
    }

    @Bean
    public Binding bindingAiQueue() {
        return BindingBuilder.bind(aiQueue())
                .to(aiDirectExchange())
                .with(RabbitMQConstant.AI_ROUTING_KEY);
    }

}
