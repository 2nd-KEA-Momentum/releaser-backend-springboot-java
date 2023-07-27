package com.momentum.releaser.domain.issue.domain;


import com.momentum.releaser.domain.issue.dto.IssueRequestDto.IssueInfoRequestDTO;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.UpdateReleaseDocsReq;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE issue SET status = 'N' WHERE issue_id=?")
@Where(clause = "status = 'Y'")
@Table(name = "issue")
@Entity
public class Issue extends BaseTime {

    @Id
    @Column(name = "issue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueId;

    @NotNull
    @Column(name = "title")
    private String title;

    @NotNull
    @Column(name = "content")
    private String content;

    @Column(name = "summary")
    private String summary;

    @NotNull
    @Column(name = "tag")
    @Enumerated(EnumType.STRING)
    private Tag tag;

    @NotNull
    @Column(name = "end_date")
    private Date endDate;

    @NotNull
    @Column(name = "life_cycle")
    @Enumerated(EnumType.STRING)
    private LifeCycle lifeCycle; //이슈 진행 상태

    @NotNull
    @Column(name = "edit")
    private char edit; //수정 여부

    @NotNull
    @Column(name = "status")
    private char status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private ProjectMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id")
    private ReleaseNote release;

    @OneToOne()
    @JoinColumn(name = "issue_num_id")
    private IssueNum issueNum;

    @OneToMany(mappedBy = "issue")
    private List<IssueOpinion> issueOpinions = new ArrayList<>();


    @Builder
    public Issue(Long issueId, String title, String content, String summary, Tag tag, Date endDate, LifeCycle lifeCycle, char edit, char status, Project project, ProjectMember member, ReleaseNote release, IssueNum issueNum) {
        this.issueId = issueId;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.tag = tag;
        this.endDate = endDate;
        this.lifeCycle = lifeCycle;
        this.edit = edit;
        this.status = status;
        this.project = project;
        this.member = member;
        this.release = release;
        this.issueNum = issueNum;
    }



    /**
     * 특정 릴리즈 노트와 이슈를 연결할 때 사용한다.
     */
    public void updateReleaseNote(ReleaseNote releaseNote) {
        this.release = releaseNote;
    }

    /**
     * 이슈 연결을 해제할 때 사용한다.
     */
    public void disconnectReleaseNote() {
        this.release = null;
    }

    /**
     * 이슈 수정
     */
    public void updateIssue(IssueInfoRequestDTO updateReq, char edit, ProjectMember member) {
        this.title = updateReq.getTitle();
        this.content = updateReq.getContent();
        this.edit = edit;
        this.tag = Tag.valueOf(updateReq.getTag().toUpperCase());
        this.endDate = updateReq.getEndDate();
        this.member = member;
    }

    /**
     * 이슈 요약
     */
    public void updateSummary(UpdateReleaseDocsReq updateReq) {
        this.summary = updateReq.getSummary();
    }


    /**
     * 이슈가 삭제 되기전 실행된다.
     */
    @PreRemove
    private void preRemove() {
        deleteToIssueNum();
        for (IssueOpinion opinion : issueOpinions) {
            opinion.statusToInactive();
        }

    }

    public void deleteToIssueNum() {
        this.issueNum = null;
    }


    public void statusToInactive() {
        this.status = 'N';
    }

    public void softDelete() {
        for (IssueOpinion opinion : issueOpinions) {
            opinion.statusToInactive();
        }

    }

    //issueNum 저장
    public void updateIssueNum(IssueNum issueNum) {
        this.issueNum = issueNum;
    }

    //issue edit 변경
    public void updateIssueEdit(char status){
        this.edit = status;
    }

    //issue lifeCycle 변경
    public void updateLifeCycle(String lifeCycle) {
        this.lifeCycle = LifeCycle.valueOf(lifeCycle);
    }

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.lifeCycle = lifeCycle == null ? LifeCycle.NOT_STARTED : this.lifeCycle;
        this.edit = (this.edit == '\0') ? 'N' : this.edit;
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }


}
