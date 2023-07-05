package com.momentum.releaser.domain.release.api;

import com.google.gson.Gson;
import com.momentum.releaser.domain.release.application.ReleaseServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@SpringBootTest
class ReleaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReleaseServiceImpl releaseService;

    @Autowired
    private Gson gson;

    @Test
    @DisplayName("5.1 프로젝트별 릴리즈 노트 목록 조회: 유효성 검사 - projectId")
    void API_5_1_유효성검사_projectId() {

        // Given

        // When

        // Then

    }

    @Test
    @DisplayName("5.2 릴리즈 노트 생성: 유효성 검사 - ")
    void API_5_2_유효성검사_projectId() {

    }
}