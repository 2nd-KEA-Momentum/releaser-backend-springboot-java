package com.momentum.releaser.domain.issue.application;

import com.momentum.releaser.domain.issue.dto.IssueRequestDto.IssueInfoReq;
import com.momentum.releaser.domain.issue.dto.IssueRequestDto.RegisterOpinionReq;
import com.momentum.releaser.domain.issue.dto.IssueResponseDto.*;

import java.util.List;

public interface IssueService {
    /**
     * 7.1 이슈 생성
     */
    IssueIdResponseDTO addIssue(Long projectId, IssueInfoReq issueInfoReq);

    /**
     * 7.2 이슈 수정
     */
    String modifyIssue(Long issueId, String email, IssueInfoReq updateReq);

    /**
     * 7.3 이슈 제거
     */
    String removeIssue(Long issueId);


    /**
     * 7.4 프로젝트별 모든 이슈 조회
     */
    GetIssuesList findAllIssues(Long projectId);



    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */
    List<GetDoneIssues> findDoneIssues(Long projectId, String status);


    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */
    List<GetConnectionIssues> findConnectIssues(Long projectId, Long releaseId);

    /**
     * 7.7 이슈별 조회
     */
    GetIssue findIssue(Long issueId, String email);


    /**
     * 7.8 이슈 상태 변경
     */
    String modifyIssueLifeCycle(Long issueId, String lifeCycle);



    /**
     * 8.1 이슈 의견 추가
     */

    List<OpinionInfoRes> registerOpinion(Long issueId, String email, RegisterOpinionReq opinionReq);


    /**
     * 8.2 이슈 의견 삭제
     */
    List<OpinionInfoRes> deleteOpinion(Long opinionId, String email);


}
