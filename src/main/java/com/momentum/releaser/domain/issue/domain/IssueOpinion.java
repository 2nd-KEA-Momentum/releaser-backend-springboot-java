package com.momentum.releaser.domain.issue.domain;

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
@Table(name = "issue_opinion")
@Entity
public class IssueOpinion extends BaseTime {

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
    @JoinColumn(name = "opinion_id")
    private ProjectMember projectMember;

    @Builder
    public IssueOpinion(String opinion, char status, ProjectMember projectMember) {
        this.opinion = opinion;
        this.status = status;
        this.projectMember = projectMember;
    }
}
