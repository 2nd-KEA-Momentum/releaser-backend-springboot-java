package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.*;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.*;

import java.util.List;

public interface ReleaseService {

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    ReleasesResponseDTO findReleaseNotes(String userEmail, Long projectId);

    /**
     * 5.2 릴리즈 노트 생성
     */
    ReleaseCreateAndUpdateResponseDTO addReleaseNote(String userEmail, Long project, ReleaseCreateRequestDTO releaseCreateRequestDto);

    /**
     * 5.3 릴리즈 노트 수정
     */
    ReleaseCreateAndUpdateResponseDTO saveReleaseNote(String userEmail, Long releaseId, ReleaseUpdateRequestDTO releaseUpdateRequestDto);

    /**
     * 5.4 릴리즈 노트 삭제
     */
    String removeReleaseNote(String userEmail, Long releaseId);

    /**
     * 5.5 릴리즈 노트 조회
     */
    ReleaseInfoResponseDTO findReleaseNote(String userEmail, Long releaseId);

    /**
     * 5.6 릴리즈 노트 배포 동의 여부 선택
     */
    List<ReleaseApprovalsResponseDTO> modifyReleaseApproval(String userEmail, Long releaseId, ReleaseApprovalRequestDTO releaseApprovalRequestDto);

    /**
     * 5.7 릴리즈 노트 그래프 좌표 추가
     */
    String modifyReleaseCoordinate(ReleaseNoteCoordinateRequestDTO releaseNoteCoordinateRequestDto);

    /**
     * 6.1 릴리즈 노트 의견 추가
     */
    List<ReleaseOpinionsResponseDTO> addReleaseOpinion(String userEmail, Long releaseId, ReleaseOpinionCreateRequestDTO releaseOpinionCreateRequestDto);

    /**
     * 6.2 릴리즈 노트 의견 삭제
     */
    List<ReleaseOpinionsResponseDTO> deleteReleaseOpinion(String userEmail, Long opinionId);

    /**
     * 6.3 릴리즈 노트 의견 목록 조회
     */
    List<ReleaseOpinionsResponseDTO> getReleaseOpinions(Long releaseId);

    List<ReleaseDocsResponseDTO> getReleaseDocs(Long projectId);

    String updateReleaseDocs(Long projectId, String email, List<UpdateReleaseDocsRequestDTO> updateReleaseDocsReq);

}
