package com.momentum.releaser.global.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    SUCCESS_TO_UPDATE_RELEASE_NOTE(true, 1400, "릴리즈 노트 수정에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */

    INVALID_REQUEST_BODY(false, 2000, "요청 데이터가 잘못되었습니다."),
    INVALID_RELEASE_VERSION_TYPE(false, 2400, "릴리즈 버전 타입이 올바르지 않습니다. MAJOR, MINOR, PATCH 중 하나여야 합니다."),
    INVALID_ISSUE_TAG(false, 2500, "이슈 태그가 올바르지 않습니다."),

    /**
     * 3000 : Response 오류
     */


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    NOT_EXISTS_USER(false, 4100, "존재하지 않는 유저입니다."),
    NOT_EXISTS_PROJECT(false, 4200, "존재하지 않는 프로젝트입니다."),
    NOT_EXISTS_PROJECT_MEMBER(false, 4300, "존재하지 않는 멤버입니다."),
    NOT_EXISTS_ADMIN_MEMBER(false, 4301, "관리자가 존재하지 않습니다."),
    NOT_EXISTS_RELEASE_NOTE(false, 4400, "존재하지 않는 릴리즈 노트입니다."),
    FAILED_TO_CREATE_RELEASE_NOTE(false, 4401, "릴리즈 노트 생성에 실패하였습니다."),
    FAILED_TO_GET_LATEST_RELEASE_VERSION(false, 4402, "릴리즈 노트 버전 불러오기에 실패하였습니다."),
    FAILED_TO_UPDATE_RELEASE_NOTE(false, 4403, "릴리즈 노트 수정에 실패하였습니다."),
    DUPLICATED_RELEASE_VERSION(false, 4404, "이미 존재하는 릴리즈 버전입니다."),
    INVALID_RELEASE_VERSION(false, 4405, "올바르지 않은 릴리즈 버전입니다."),
    NOT_EXISTS_ISSUE(false, 4500, "존재하지 않는 이슈입니다."),
    INVALID_ISSUE_WITH_COMPLETED(false, 4501, "이미 연결된 이슈가 포함되어 있습니다."),
    INVALID_ISSUE_WITH_NOT_DONE(false, 4502, "완료되지 않은 이슈는 연결할 수 없습니다."),
    FAILED_TO_CONNECT_ISSUE_WITH_RELEASE_NOTE(false, 4501, "이슈 연결에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
