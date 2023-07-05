package com.momentum.releaser.domain.release.domain;

import com.momentum.releaser.domain.issue.domain.Issue;
import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.domain.release.domain.ReleaseEnum.ReleaseDeployStatus;
import com.momentum.releaser.global.common.BaseTime;
import com.sun.istack.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE release_note SET status = 'N' WHERE release_id=?")
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
    @Column(name = "deploy_status")
    @Enumerated(EnumType.STRING)
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
    public ReleaseNote(Long releaseId, String title, String content, String summary, String version, Date deployDate, Project project) {
        this.releaseId = releaseId;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.version = version;
        this.deployDate = deployDate;
        this.project = project;
    }

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.deployStatus = (this.deployStatus == null) ? ReleaseDeployStatus.PLANNING : this.deployStatus;
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }
}
