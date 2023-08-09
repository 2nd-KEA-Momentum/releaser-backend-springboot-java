package com.momentum.releaser.rabbitmq.sample;

import javax.validation.Valid;
import javax.validation.constraints.Email;

import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.rabbitmq.MessageDto.ReleaserMessageDto;
import com.momentum.releaser.rabbitmq.MessageDto.SampleMessageDto;
import com.momentum.releaser.rabbitmq.MessageService;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth/messages")
@RestController
public class SampleMessageController {

    private final SampleMessageService sampleMessageService;
    private final MessageService messageService;

    /**
     * Queue로 메시지를 발행
     *
     * @param sampleMessageDto 발행할 메시지의 DTO 객체
     * @return 메시지 전달 성공 응답
     * @author seonwoo
     * @date 2023-08-04 (금)
     */
    @PostMapping(value = "/sample")
    public BaseResponse<String> sendSampleMessage(@Valid @RequestBody SampleMessageDto sampleMessageDto) {
        sampleMessageService.sendSampleMessage(sampleMessageDto);
        return new BaseResponse<>("RabbitMQ 메시지 전달에 성공하였습니다.");
    }

    /**
     * Queue로 메시지를 발행
     *
     * @param email              사용자 이메일
     * @param releaserMessageDto 발행할 메시지의 DTO 객체
     * @return 메시지 전달 성공 응답
     * @author seonwoo
     * @date 2023-08-07 (월)
     */
    @PostMapping(value = "/sample/user")
    public BaseResponse<String> sendSampleUserMessage(
            @Email @RequestParam String email,
            @Valid @RequestBody ReleaserMessageDto releaserMessageDto) {
        messageService.sendMessagePerUser(email, releaserMessageDto);
        return new BaseResponse<>("RabbitMQ 메시지 전달에 성공하였습니다.");
    }
}
