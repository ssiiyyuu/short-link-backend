package com.siyu.server.mq.producer;

import com.siyu.rabbitMQ.constants.RabbitMQConstants;
import com.siyu.server.entity.VisitorOperationLog;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorOperationLogProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendVisitorOperationLogSaveMessage(VisitorOperationLog visitorOperationLog) {
        rabbitTemplate.convertAndSend(RabbitMQConstants.VISITOR_OPERATION_LOG_EXCHANGE, RabbitMQConstants.VISITOR_OPERATION_LOG_SAVE_KEY, visitorOperationLog);
    }
}
