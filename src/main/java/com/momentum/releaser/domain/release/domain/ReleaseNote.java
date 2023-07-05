package com.momentum.releaser.domain.release.domain;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "status = 'Y'")
@Table(name = "release_note")
@Entity
public class ReleaseNote extends BaseTime {

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

    @Column(name = "summary")
    private String summary;

    @NotNull
    @Column(name = "version")
    private String version;

    @Column(name = "deploy_date")
    private Date deployDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "deploy_status")
    private ReleaseDeployStatus deployStatus;

    @NotNull
    @Column(name = "status")
    private char status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "release")
    private List<ReleaseOpinion> opinions = new ArrayList<>();

    @OneToMany(mappedBy = "release")
    private List<Issue> issues = new ArrayList<>();

    @Builder
    public ReleaseNote(String title, String content, String summary, String version, Date deployDate, Project project) {
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.version = version;
        this.deployDate = deployDate;
        this.project = project;
    }

    /**
     * Insert 되기 전에 (persist 되기 전에) 실행된다.
     */
    @PrePersist
    public void init() {
        this.deployStatus = String.valueOf(this.deployStatus).equals("") ? ReleaseDeployStatus.PLANNING : this.deployStatus;
        this.status = String.valueOf(this.status).equals("") ? 'Y' : this.status;
    }
}
