package com.momentum.releaser.domain.release.dao.approval;

import com.momentum.releaser.domain.release.domain.ReleaseApproval;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="release-approval", path="release-approval")
public interface ReleaseApprovalRepository extends JpaRepository<ReleaseApproval, Long>, ReleaseApprovalCustom {
}
