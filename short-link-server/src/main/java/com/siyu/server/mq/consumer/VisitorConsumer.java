package com.siyu.server.mq.consumer;

import com.siyu.rabbitMQ.constants.RabbitMQConstants;
import com.siyu.redis.constants.RedisKeyConstants;
import com.siyu.server.entity.Visitor;
import com.siyu.server.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitorConsumer {

    private final VisitorService visitorService;

    private final RBloomFilter<String> visitorBloomFilter;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = RabbitMQConstants.VISITOR_SAVE_QUEUE),
            exchange = @Exchange(name = RabbitMQConstants.VISITOR_EXCHANGE),
            key = RabbitMQConstants.VISITOR_SAVE_KEY
    ))
    public void saveVisitor(Visitor visitor) {
        //1. 保存到数据库
        visitorService.save(visitor);
        //2. 保存到布隆过滤器中
        visitorBloomFilter.add(visitor.getUuid());
    }
}
