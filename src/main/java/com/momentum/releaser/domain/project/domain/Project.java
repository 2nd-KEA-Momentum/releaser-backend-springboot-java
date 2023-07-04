package com.momentum.releaser.domain.project.domain;


import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.dto.ProjectReqDto;
import com.momentum.releaser.domain.project.dto.ProjectReqDto.ProjectInfoReq;
import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    public Project(String title, String team, String img, char status) {
        this.title = title;
        this.team = team;
        this.img = img;
        this.status = status;
    }

    public void updateProject(ProjectInfoReq updateReq) {
        this.title = updateReq.getTitle();
        this.team = updateReq.getTeam();
        this.img = updateReq.getImg();
    }
}
