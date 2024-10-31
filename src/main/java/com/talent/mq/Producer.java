package com.talent.mq;

import com.talent.constant.RabbitMQConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class Producer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessageToAPI(String message) {
        log.info("Sending message:{}, exchange:{}, routeKey:{}",
                message,
                RabbitMQConstant.API_EXCHANGE_NAME,
                RabbitMQConstant.API_ROUTING_KEY);
        rabbitTemplate.convertAndSend(
                RabbitMQConstant.API_EXCHANGE_NAME,
                RabbitMQConstant.API_ROUTING_KEY,
                message);
    }

    public void sendMessageToAI(String message) {
        log.info("Sending message:{}, exchange:{}, routeKey:{}",
                message,
                RabbitMQConstant.AI_EXCHANGE_NAME,
                RabbitMQConstant.AI_ROUTING_KEY);
        rabbitTemplate.convertAndSend(
                RabbitMQConstant.AI_EXCHANGE_NAME,
                RabbitMQConstant.AI_ROUTING_KEY,
                message);
    }

}
