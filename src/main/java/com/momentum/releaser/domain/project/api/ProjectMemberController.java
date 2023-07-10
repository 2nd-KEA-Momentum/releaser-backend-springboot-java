package com.momentum.releaser.domain.project.api;

import com.momentum.releaser.domain.project.application.ProjectMemberService;
import com.momentum.releaser.domain.project.dto.ProjectResDto;
import com.momentum.releaser.domain.project.dto.ProjectResDto.GetMembersRes;
import com.momentum.releaser.global.config.BaseResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Validated
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    /**
     * 4.1 프로젝트 멤버 조회
     */
    @GetMapping("/{memberId}/project/{projectId}")
    public BaseResponse<List<GetMembersRes>> getMembers(@PathVariable @Min(1) Long projectId,
                                                        @PathVariable @Min(2) Long memberId) {
        return new BaseResponse<>(projectMemberService.getMembers(memberId, projectId));
    }

    /**
     * 4.2 프로젝트 멤버 추가
     */

    /**
     * 4.3 프로젝트 멤버 제거
     */
    @PostMapping("/{memberId}")
    public BaseResponse<String> deleteProjectMember(@PathVariable @Min(1) Long memberId) {
        return new BaseResponse<>(projectMemberService.deleteMember(memberId));
    }

    /**
     * 4.4 프로젝트 멤버 탈퇴
     */
    @PostMapping("/{userId}/project/{projectId}/withdraw")
    public BaseResponse<String> withdrawProjectMember(
            @PathVariable @Min(1) Long userId,
            @PathVariable @Min(1) Long projectId){
        return new BaseResponse<>(projectMemberService.withdrawMember(userId, projectId));
    }
}
