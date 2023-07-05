package com.momentum.releaser.domain.project.domain;


import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
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
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE project SET status = 'N' WHERE project_id=?")
@Where(clause = "status = 'Y'")
@Table(name = "project")
@Entity
public class Project extends BaseTime {

    @Id
    @Column(name = "project_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @NotNull
    @Column(name = "title")
    private String title;

    @NotNull
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "team")
    private String team;

    @Column(name = "img")
    private String img;

    @NotNull
    @Column(name = "status")
    private char status;

    @OneToMany(mappedBy = "project")
    private List<ProjectMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<ReleaseNote> releases = new ArrayList<>();

    @OneToMany(mappedBy = "project")
    private List<Issue> issues = new ArrayList<>();

    @Builder
    public Project(String title, String content, String team, String img, char status) {
        this.title = title;
        this.content = content;
        this.team = team;
        this.img = img;
        this.status = status;
    }

    public void updateProject(ProjectInfoReq updateReq) {
        this.title = updateReq.getTitle();
        this.content = updateReq.getContent();
        this.team = updateReq.getTeam();
        this.img = updateReq.getImg();
    }

    @PreRemove
    private void preRemoveMember() {
        for (ProjectMember member : members) {
            member.statusToInactive();
        }
    }

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }
}
