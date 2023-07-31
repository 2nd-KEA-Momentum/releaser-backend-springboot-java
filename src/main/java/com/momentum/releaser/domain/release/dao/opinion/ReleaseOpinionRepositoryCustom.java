package com.momentum.releaser.domain.release.dao.opinion;

import com.momentum.releaser.domain.release.domain.ReleaseNote;
import com.momentum.releaser.domain.release.dto.ReleaseDataDto.ReleaseOpinionsDataDTO;

import java.util.List;

/**
 * @see ReleaseOpinionRepositoryImpl
 */
public interface ReleaseOpinionRepositoryCustom {

    List<ReleaseOpinionsDataDTO> getDtosByReleaseNote(ReleaseNote releaseNote);
}
