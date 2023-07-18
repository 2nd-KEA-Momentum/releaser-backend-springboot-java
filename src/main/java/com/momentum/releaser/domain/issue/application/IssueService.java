package com.momentum.releaser.domain.issue.application;

import com.momentum.releaser.domain.issue.dto.IssueReqDto;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.IssueInfoReq;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.RegisterOpinionReq;
import com.momentum.releaser.domain.issue.dto.IssueResDto;
import com.momentum.releaser.domain.issue.dto.IssueResDto.*;

import java.util.List;

public interface IssueService {
    /**
     * 7.1 이슈 생성
     */
    String registerIssue(Long projectId, IssueInfoReq issueInfoReq);

    /**
     * 7.2 이슈 수정
     */
    String updateIssue(Long issueId, String email, IssueInfoReq updateReq);

    /**
     * 7.3 이슈 제거
     */
    String deleteIssue(Long issueId);


    /**
     * 7.4 프로젝트별 모든 이슈 조회
     */
    GetIssuesList getIssues(Long projectId);



    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */
    List<GetDoneIssues> getDoneIssues(Long projectId);


    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */
    List<GetConnectionIssues> getConnectRelease(Long projectId, Long releaseId);

    /**
     * 7.7 이슈별 조회
     */
    GetIssue getIssue(Long issueId, String email);


    /**
     * 7.8 이슈 상태 변경
     */
    String updateLifeCycle(Long issueId, String lifeCycle);



    /**
     * 8.1 이슈 의견 추가
     */

    List<OpinionInfoRes> registerOpinion(Long issueId, String email, RegisterOpinionReq opinionReq);


    /**
     * 8.2 이슈 의견 삭제
     */
    String deleteOpinion(Long opinionId, String email);


}
