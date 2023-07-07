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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
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
    @NotBlank(message = "릴리즈 제목을 입력해 주세요.")
    @Size(min = 1, max = 45, message = "릴리즈 제목은 1자 이상 45자 이하여야 합니다.")
    @Column(name = "title")
    private String title;

    @NotNull
    @Size(max = 1000, message = "릴리즈 설명은 1000자를 넘을 수 없습니다.")
    @Column(name = "content")
    private String content;

    @Size(max = 100, message = "릴리즈 요약은 100자를 넘을 수 없습니다.")
    @Column(name = "summary")
    private String summary;

    @NotNull
    @Pattern(regexp = "^(?!0)\\d+\\.\\d+\\.\\d+$", message = "릴리즈 버전 형식에 맞지 않습니다.")
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

    @Column(name = "coord_x")
    private Double coordX;

    @Column(name = "coord_y")
    private Double coordY;

    @OneToMany(mappedBy = "release")
    private List<ReleaseOpinion> releaseOpinions = new ArrayList<>();

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

    @PreRemove
    private void preRemove() {
        for (ReleaseOpinion opinion : releaseOpinions) {
            opinion.statusToInactive();
        }
        for (Issue issue : issues) {
            issue.statusToInactive();
        }
    }

    public void softDelete() {
        for (ReleaseOpinion opinion : releaseOpinions) {
            opinion.statusToInactive();
        }
    }

    public void statusToInactive() {
        this.status = 'N';
    }

    /**
     * insert 되기전 (persist 되기전) 실행된다.
     */
    @PrePersist
    public void prePersist() {
        this.deployStatus = (this.deployStatus == null) ? ReleaseDeployStatus.PLANNING : this.deployStatus;
        this.status = (this.status == '\0') ? 'Y' : this.status;
    }

    /**
     * 릴리즈 노트 정보를 업데이트할 때 사용한다.
     */
    public void updateReleaseNote(String title, String content, String summary, String version, Date deployDate) {
        this.title = title;
        this.content = content;
        this.version = version;
        this.summary = summary;
        this.deployDate = deployDate;
    }
}
