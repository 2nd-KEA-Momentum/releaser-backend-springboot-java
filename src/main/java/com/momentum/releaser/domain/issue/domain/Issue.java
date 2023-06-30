package com.momentum.releaser.domain.issue.domain;


import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.project.domain.ProjectMember;
import com.momentum.releaser.domain.release.domain.Release;
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
    private String tag;

    @NotNull
    @Column(name = "end_date")
    private Date endDate;

    @NotNull
    @Column(name = "status")
    private char status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private ProjectMember member;

    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Release release;

    @OneToMany(mappedBy = "issue")
    private List<ReleaseOpinion> opinions = new ArrayList<>();


    @Builder
    public Issue(String title, String content, String tag, Date endDate, char status, Project project, ProjectMember member, Release release) {
        this.title = title;
        this.content = content;
        this.tag = tag;
        this.endDate = endDate;
        this.status = status;
        this.project = project;
        this.member = member;
        this.release = release;
    }
}
