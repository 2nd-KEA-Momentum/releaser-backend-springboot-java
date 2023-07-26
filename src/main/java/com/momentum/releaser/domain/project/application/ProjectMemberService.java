package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.InviteProjectMemberResponseDTO;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.MembersResponseDTO;

import java.util.List;

public interface ProjectMemberService {
    /**
     * 4.1 프로젝트 멤버 조회
     */
    List<MembersResponseDTO> findProjectMembers(Long projectId, String email);

    /**
     * 4.2 프로젝트 멤버 추가
     */
    InviteProjectMemberResponseDTO addProjectMember(String link, String email);


    /**
     * 4.3 프로젝트 멤버 제거
     */
    String removeProjectMember(Long memberId, String email);

    /**
     * 4.4 프로젝트 멤버 탈퇴
     */
    String removeWithdrawProjectMember(Long projectId, String email);

}
