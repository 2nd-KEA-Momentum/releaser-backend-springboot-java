package com.momentum.releaser.rabbitmq.sample;

import com.momentum.releaser.rabbitmq.MessageDto.SampleMessageDto;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class SampleMessageService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.sample.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.sample.routing.key}")
    private String routingKey;

    /**
     * Queue로 메시지 발행
     *
     * @param sampleMessageDto 발행할 샘플 메시지의 DTO 객체
     * @author seonwoo
     * @date 2023-08-04 (금)
     */
    public void sendSampleMessage(SampleMessageDto sampleMessageDto) {
        log.info("message sent: {}", sampleMessageDto.toString());
        rabbitTemplate.convertAndSend(exchangeName, routingKey, sampleMessageDto);
    }

    /**
     * Queue에서 메시지를 구독
     *
     * @param sampleMessageDto 구독한 메시지를 담고 있는 샘플 메시지 DTO 객체
     * @author seonwoo
     * @date 2023-08-04 (금)
     */
    @RabbitListener(queues = "${rabbitmq.sample.queue.name}")
    public void receiveSampleMessage(SampleMessageDto sampleMessageDto) {
        log.info("Received message: {}", sampleMessageDto.toString());
    }
}
