package com.siyu.server.mq.producer;

import com.siyu.common.domain.dto.VisitorInfoDTO;
import com.siyu.rabbitMQ.constants.RabbitMQConstants;
import com.siyu.server.dto.LinkLogMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkLogProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendLinkLogSaveMessage(String shortUrl, VisitorInfoDTO visitorInfoDTO) {
        LinkLogMessageDTO linkLogMessageDTO = new LinkLogMessageDTO();
        linkLogMessageDTO.setShortUrl(shortUrl);
        linkLogMessageDTO.setVisitorInfoDTO(visitorInfoDTO);
        rabbitTemplate.convertAndSend(RabbitMQConstants.LINK_LOG_EXCHANGE, "", linkLogMessageDTO);
    }
}
