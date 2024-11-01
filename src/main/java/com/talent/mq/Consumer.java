package com.talent.mq;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONObject;
import com.rabbitmq.client.Channel;
import com.talent.constant.RabbitMQConstant;
import com.talent.manager.RedisManager;
import com.talent.model.dto.User;
import com.talent.model.vo.DescriptionVO;
import com.talent.service.AIService;
import com.talent.service.UserService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class Consumer {

    @Resource
    private UserService userService;

    @Resource
    private AIService aiService;

    @Resource
    private RedisManager redisManager;

    @SneakyThrows
    @RabbitListener(queues = {RabbitMQConstant.API_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessageAPI(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long delivery) {
        log.info("receivedMessage message, exchange:{}, routeKey:{}",
                message,
                RabbitMQConstant.API_EXCHANGE_NAME,
                RabbitMQConstant.API_ROUTING_KEY);

        String[] messages = message.split(" ");

        HttpResponse response = HttpRequest.get(messages[0])
                .header("Accept", "application/vnd.github.v3+json")
                .header("Authorization", "Bearer " + messages[1])
                .execute();

        if (response.getStatus() == HttpStatus.HTTP_NOT_FOUND) {
            log.info("请求url出错");
            channel.basicNack(delivery, false, false);
            return;
        }

        if (response.getStatus() != HttpStatus.HTTP_OK) {
            log.info("爬取失败");
            channel.basicNack(delivery, false, true);
            return;
        }

        String body = response.body();
        if (StringUtils.isAnyBlank(body)) {
            channel.basicAck(delivery, false);
            return;
        }

        JSONObject userJSON = new JSONObject(body);

        // 存入数据库
        userService.saveOrUpdate(User.parseUser(userJSON));

        channel.basicAck(delivery, false);
    }

    @SneakyThrows
    @RabbitListener(queues = {RabbitMQConstant.AI_QUEUE_NAME}, ackMode = "MANUAL")
    public void receiveMessageAI(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long delivery) {
        log.info("receivedMessage message, exchange:{}, routeKey:{}",
                message,
                RabbitMQConstant.AI_EXCHANGE_NAME,
                RabbitMQConstant.AI_ROUTING_KEY);

        String[] messages = message.split(" ");

        String login = messages[0];
        String token = messages[1];
        String id = messages[2];

        String description = aiService.getDescription(login, token);
        log.info(description);
        JSONObject desJSON = new JSONObject(description);
        User userInfo = userService.getUserInfo(login, token);

        DescriptionVO descriptionVO = DescriptionVO.parseDescriptionVO(userInfo, desJSON);
        redisManager.cacheTaskStatus(id, descriptionVO);

        channel.basicAck(delivery, false);
    }

}
