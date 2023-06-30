package com.momentum.releaser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 여기서 @EnableJpaAuditing은 BaseTime 클래스처럼 JPA auditing 기능을 활성화하기 위한 어노테이션이다.
 */
@EnableJpaAuditing
@SpringBootApplication
public class ReleaserApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReleaserApplication.class, args);
    }

}
