package com.momentum.releaser.redis.notification;

import org.springframework.data.repository.CrudRepository;

public interface NotificationRedisRepository extends CrudRepository<Notification, String> {

    /**
     * Notification 데이터를 가져온다.
     *
     * @param notificationId 알림 식별 번호
     * @return Notification
     * @author seonwoo
     * @date 2023-08-09 (수)
     */
    Notification findByNotificationId(String notificationId);
}
