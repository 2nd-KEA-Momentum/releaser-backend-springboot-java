package com.momentum.releaser.domain.project.api;

import com.momentum.releaser.domain.project.application.ProjectMemberService;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.InviteProjectMemberRes;
import com.momentum.releaser.domain.project.dto.ProjectMemberResponseDto.MembersResponseDTO;
import com.momentum.releaser.global.config.BaseResponse;
import com.momentum.releaser.global.jwt.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
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
    @GetMapping("/project/{projectId}")
    public BaseResponse<List<MembersResponseDTO>> getMembers(@PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectMemberService.getMembers(projectId, email));
    }

    /**
     * 4.2 프로젝트 멤버 추가
     */
    @PostMapping("/join/{link}")
    public BaseResponse<InviteProjectMemberRes> inviteMember(@PathVariable String link,
                                                             @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        String message = "프로젝트 참여가 완료되었습니다.";
        return new BaseResponse<>(projectMemberService.addMember(link, email), message);
    }


    /**
     * 4.3 프로젝트 멤버 제거
     */
    @PostMapping("/{memberId}")
    public BaseResponse<String> deleteProjectMember(@PathVariable @Min(value = 1, message = "프로젝트 멤버 식별 번호는 1 이상의 숫자여야 합니다.") Long memberId,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectMemberService.deleteMember(memberId, email));
    }

    /**
     * 4.4 프로젝트 멤버 탈퇴
     */
    @PostMapping("/project/{projectId}/withdraw")
    public BaseResponse<String> withdrawProjectMember(
            @PathVariable @Min(value = 1, message = "프로젝트 식별 번호는 1 이상의 숫자여야 합니다.") Long projectId,
            @AuthenticationPrincipal UserPrincipal userPrincipal){
        String email = userPrincipal.getEmail();
        return new BaseResponse<>(projectMemberService.withdrawMember(projectId, email));
    }
}
