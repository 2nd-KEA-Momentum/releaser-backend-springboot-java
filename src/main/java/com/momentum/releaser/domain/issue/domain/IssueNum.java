package com.momentum.releaser.domain.issue.domain;


import com.momentum.releaser.domain.project.domain.Project;
import com.momentum.releaser.global.common.BaseTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "issue_num")
@Entity
public class IssueNum extends BaseTime {

    @Id
    @Column(name = "issue_num_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long issueNumId;

    @OneToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    private Long issueNum;

    @Builder
    public IssueNum(Long issueNumId, Issue issue, Project project, Long issueNum) {
        this.issueNumId = issueNumId;
        this.issue = issue;
        this.project = project;
        this.issueNum = issueNum;
    }

    @PreRemove
    private void preRemove() {
        if (issue != null) {
            issue.deleteToIssueNum();
        }
        project.removeIssueNum(this);
    }



    public void deleteToProject() {
        this.project = null;
        this.issue = null;
    }

}
