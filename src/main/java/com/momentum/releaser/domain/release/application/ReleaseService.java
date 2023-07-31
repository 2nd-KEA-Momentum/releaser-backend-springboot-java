package com.momentum.releaser.domain.release.application;

import com.momentum.releaser.domain.release.dto.ReleaseRequestDto.*;
import com.momentum.releaser.domain.release.dto.ReleaseResponseDto.*;

import java.util.List;

public interface ReleaseService {

    /**
     * 5.1 프로젝트별 릴리즈 노트 목록 조회
     */
    ReleasesResponseDto findReleaseNotes(String userEmail, Long projectId);

    /**
     * 5.2 릴리즈 노트 생성
     */
    ReleaseCreateAndUpdateResponseDto addReleaseNote(String userEmail, Long project, ReleaseCreateRequestDto releaseCreateRequestDto);

    /**
     * 5.3 릴리즈 노트 수정
     */
    ReleaseCreateAndUpdateResponseDto saveReleaseNote(String userEmail, Long releaseId, ReleaseUpdateRequestDto releaseUpdateRequestDto);

    /**
     * 5.4 릴리즈 노트 삭제
     */
    String removeReleaseNote(String userEmail, Long releaseId);

    /**
     * 5.5 릴리즈 노트 조회
     */
    ReleaseInfoResponseDto findReleaseNote(String userEmail, Long releaseId);

    /**
     * 5.6 릴리즈 노트 배포 동의 여부 선택
     */
    List<ReleaseApprovalsResponseDto> modifyReleaseApproval(String userEmail, Long releaseId, ReleaseApprovalRequestDto releaseApprovalRequestDto);

    /**
     * 5.7 릴리즈 노트 그래프 좌표 추가
     */
    String modifyReleaseCoordinate(ReleaseNoteCoordinateRequestDto releaseNoteCoordinateRequestDto);

    /**
     * 6.1 릴리즈 노트 의견 추가
     */
    List<ReleaseOpinionsResponseDto> addReleaseOpinion(String userEmail, Long releaseId, ReleaseOpinionCreateRequestDto releaseOpinionCreateRequestDto);

    /**
     * 6.2 릴리즈 노트 의견 삭제
     */
    List<ReleaseOpinionsResponseDto> deleteReleaseOpinion(String userEmail, Long opinionId);

    /**
     * 6.3 릴리즈 노트 의견 목록 조회
     */
    List<ReleaseOpinionsResponseDto> getReleaseOpinions(Long releaseId);

    List<ReleaseDocsRes> getReleaseDocs(Long projectId);

    String updateReleaseDocs(Long projectId, String email, List<UpdateReleaseDocsReq> updateReleaseDocsReq);

}
