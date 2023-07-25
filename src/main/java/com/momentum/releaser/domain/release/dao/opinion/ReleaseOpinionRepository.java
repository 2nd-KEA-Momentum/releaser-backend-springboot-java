package com.momentum.releaser.domain.release.dao.opinion;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.domain.ReleaseOpinion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel="release-opinion", path="release-opinion")
public interface ReleaseOpinionRepository extends JpaRepository<ReleaseOpinion, Long>, ReleaseOpinionRepositoryCustom {

    List<ReleaseOpinion> findAllByRelease(ReleaseNote release);

}
