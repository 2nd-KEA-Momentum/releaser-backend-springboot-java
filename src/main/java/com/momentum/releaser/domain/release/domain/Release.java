package com.momentum.releaser.domain.release.domain;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.domain.Project;
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
@Table(name = "release")
@Entity
public class Release extends BaseTime {

    @Id
    @Column(name = "release_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long releaseId;

    @NotNull
    @Column(name = "title")
    private String title;

    @NotNull
    @Column(name = "content")
    private String content;

    @NotNull
    @Column(name = "version")
    private String version;

    @NotNull
    @Column(name = "status")
    private char status;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "release")
    private List<ReleaseOpinion> opinions = new ArrayList<>();

    @OneToMany(mappedBy = "release")
    private List<Issue> issues = new ArrayList<>();

    @Builder
    public Release(String title, String content, String version, char status, Project project) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.status = status;
        this.project = project;
    }

}
