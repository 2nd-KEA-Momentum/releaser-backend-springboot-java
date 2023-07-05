package com.momentum.releaser.domain.issue.application;

import com.momentum.releaser.domain.issue.dao.IssueOpinionRepository;
import com.momentum.releaser.domain.issue.dao.IssueRepository;
import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.issue.domain.IssueOpinion;
import com.momentum.releaser.domain.issue.dto.IssueReqDto;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.IssueInfoReq;
import com.momentum.releaser.domain.issue.dto.IssueReqDto.RegisterOpinionReq;
import com.momentum.releaser.domain.project.dao.ProjectMemberRepository;
import com.momentum.releaser.domain.project.dao.ProjectRepository;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.global.config.BaseResponseStatus;
import com.momentum.releaser.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_EXISTS_ISSUE;
import static com.momentum.releaser.global.config.BaseResponseStatus.NOT_EXISTS_PROJECT_MEMBER;

@Slf4j
@Service
//final 있는 필드만 생성자 만들어줌
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService{

    private final IssueRepository issueRepository;
    private final IssueOpinionRepository issueOpinionRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;

    /**
     * 7.1 이슈 생성
     */
    @Override
    @Transactional
    public String registerIssue(Long projectId, IssueInfoReq issueInfoReq) {

        //member 정보
        ProjectMember projectMember = projectMemberRepository.findById(issueInfoReq.getMemberId()).orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT_MEMBER));

        //project 정보
        Project project = projectRepository.findById(projectId).orElseThrow(() -> new CustomException(BaseResponseStatus.NOT_EXISTS_PROJECT));

        Issue newIssue = issueRepository.save(Issue.builder()
                        .title(issueInfoReq.getTitle())
                        .content(issueInfoReq.getContent())
                        .tag(issueInfoReq.getTag())
                        .endDate(issueInfoReq.getEndDate())
                        .project(project)
                        .member(projectMember)
                .build());


        log.debug("newOpinion : {}", newIssue);
        String result = "이슈 생성이 완료되었습니다.";
        return result;
    }

    /**
     * 7.2 이슈 수정
     */
    @Override
    @Transactional
    public String updateIssue(Long issueId, IssueInfoReq updateReq) {
        //issue 정보
        Issue issue = issueRepository.findById(issueId).orElseThrow(() -> new CustomException(NOT_EXISTS_ISSUE));

        //member 정보
        ProjectMember member = projectMemberRepository.findById(updateReq.getMemberId()).orElseThrow(() -> new CustomException(NOT_EXISTS_PROJECT_MEMBER));

        //issue update
        issue.updateIssue(updateReq, member);
        String result = "이슈 수정이 완료되었습니다.";
        return result;
    }
    /**
     * 7.3 이슈 제거
     */

    /**
     * 7.4 프로젝트별 모든 이슈 조회
     */

    /**
     * 7.5 프로젝트별 해결 & 미연결 이슈 조회
     */

    /**
     * 7.6 릴리즈 노트별 연결된 이슈 조회
     */

    /**
     * 7.7 이슈 검색
     */

    /**
     * 8.1 이슈 의견 추가
     */

    /**
     * 8.2 이슈 의견 삭제
     */

    /**
     * 8.3 이슈 의견 조회
     */
}
