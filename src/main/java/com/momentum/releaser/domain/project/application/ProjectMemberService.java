package com.momentum.releaser.domain.project.application;

import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;

import java.util.List;

public interface ProjectMemberService {
    /**
     * 4.1 프로젝트 멤버 조회
     */
    List<GetMembersRes> getMembers(Long projectId);

    /**
     * 4.2 프로젝트 멤버 추가
     */

    /**
     * 4.3 프로젝트 멤버 제거
     */
    String deleteMember(Long memberId);
}
