package com.momentum.releaser.domain.issue.domain;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE issue_opinion SET status = 'N' WHERE issue_opinion_id=?")
@Where(clause = "status = 'Y'")
@Table(name = "issue_opinion")
@Entity
public class IssueOpinion extends BaseTime {

    @Id
    @Column(name = "issue_opinion_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueOpinionId;

    @NotNull
    @Column(name = "opinion")
    private String opinion;

    @NotNull
    @Column(name = "status")
    private char status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opinion_id")
    private ProjectMember projectMember;

    @Builder
    public IssueOpinion(String opinion, char status, ProjectMember projectMember) {
        this.opinion = opinion;
        this.status = status;
        this.projectMember = projectMember;
    }

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }
}
