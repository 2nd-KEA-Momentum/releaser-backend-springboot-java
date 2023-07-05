package com.momentum.releaser.domain.issue.domain;


import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    @Column(name = "resolve")
    private char resolve; //해결, 미해결

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

    @OneToMany(mappedBy = "issue")
    private List<ReleaseOpinion> opinions = new ArrayList<>();


    @Builder
    public Issue(Long issueId, String title, String content, Tag tag, Date endDate, LifeCycle lifeCycle, char resolve, char status, Project project, ProjectMember member, ReleaseNote release) {
        this.issueId = issueId;
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.endDate = endDate;
        this.lifeCycle = lifeCycle;
        this.resolve = resolve;
        this.status = status;
        this.project = project;
        this.member = member;
        this.release = release;
    }


    /**
     * 특정 릴리즈 노트와 이슈를 연결할 때 사용한다.
     */
    public void updateReleaseNote(ReleaseNote releaseNote) {
        this.release = releaseNote;
    }

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.lifeCycle = lifeCycle == null ? LifeCycle.Not_Started : this.lifeCycle;
        this.resolve = (this.resolve == '\0') ? 'N' : this.resolve;
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }
}
