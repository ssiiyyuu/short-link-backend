package com.siyu.server.mq.consumer;

import com.siyu.rabbitMQ.constants.RabbitMQConstants;
import com.siyu.server.entity.VisitorOperationLog;
import com.siyu.server.service.VisitorOperationLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorOperationLogConsumer {

    private final VisitorOperationLogService visitorOperationLogService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.VISITOR_OPERATION_LOG_SAVE_QUEUE),
            exchange = @Exchange(name = RabbitMQConstants.VISITOR_OPERATION_LOG_EXCHANGE),
            key = RabbitMQConstants.VISITOR_OPERATION_LOG_SAVE_KEY
    ))
    public void saveVisitorOperationLog(VisitorOperationLog visitorOperationLog) {
        visitorOperationLogService.save(visitorOperationLog);
    }
}
