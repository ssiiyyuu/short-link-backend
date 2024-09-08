package com.siyu.server.mq.producer;

import com.siyu.rabbitMQ.constants.RabbitMQConstants;
import com.siyu.server.entity.Visitor;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendVisitorSaveMessage(Visitor visitor) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.VISITOR_EXCHANGE, RabbitMQConstants.VISITOR_SAVE_KEY, visitor);
    }

}
