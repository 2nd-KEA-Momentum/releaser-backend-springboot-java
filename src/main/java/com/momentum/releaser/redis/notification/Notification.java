package com.momentum.releaser.redis.notification;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RedisHash(value = "notification")
public class Notification {

    @Id
    private String notificationId;

    private String type;

    private String projectTitle;

    private String projectImg;

    private String message;

    private String date;

    private Map<String, Integer> markByUsers = new HashMap<>();

    @TimeToLive
    private long expiredTime;

    @Builder
    public Notification(String notificationId, String type, String projectTitle, String projectImg, String message, String date, HashMap<String, Integer> markByUsers, long expiredTime) {
        this.notificationId = notificationId;
        this.type = type;
        this.projectTitle = projectTitle;
        this.projectImg = projectImg;
        this.message = message;
        this.date = date;
        this.markByUsers = markByUsers;
        this.expiredTime = expiredTime;
    }
}
