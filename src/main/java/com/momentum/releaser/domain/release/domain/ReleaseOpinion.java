package com.momentum.releaser.domain.release.domain;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'Y'")
@Table(name = "release_opinion")
@Entity
public class ReleaseOpinion extends BaseTime {

    @Id
    @Column(name = "opinion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long opinionId;

    @NotNull
    @Column(name = "opinion")
    private String opinion;

    @NotNull
    @Column(name = "status")
    private char status;

    @ManyToOne
    @JoinColumn(name = "release_id")
    private Release release;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private ProjectMember member;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @Builder
    public ReleaseOpinion(String opinion, char status, Release release, ProjectMember member, Issue issue) {
        this.opinion = opinion;
        this.status = status;
        this.release = release;
        this.member = member;
        this.issue = issue;
    }
}
