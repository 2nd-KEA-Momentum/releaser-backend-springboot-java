package com.momentum.releaser.domain.release.domain;

import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "release_approval")
@Entity
public class ReleaseApproval extends BaseTime {

    @Id
    @Column(name = "approval_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long approvalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private ProjectMember member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id")
    private ReleaseNote release;

    @Column(name = "approval")
    private char approval;

    @Builder
    public ReleaseApproval(ProjectMember member, ReleaseNote release) {
        this.member = member;
        this.release = release;
    }

    @PreRemove
    private void preRemove() {
        release.removeReleaseApproval(this);
        member.removeReleaseApproval(this);
    }

    public void deleteToProject() {
        this.release = null;
        this.member = null;
    }

    public void deleteToMember() {
        this.release = null;
        this.member = null;
    }
    @PrePersist
    public void prePersist() {
        this.approval = (this.approval == '\0') ? 'N' : this.approval;
    }
}
