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
    @GetMapping("/{projectId}")
    public BaseResponse<List<GetMembersRes>> getMembers(@PathVariable @NotNull(message = "요청 데이터가 잘못되었습니다.") Long projectId) {
        return new BaseResponse<>(projectMemberService.getMembers(projectId));
    }

    /**
     * 4.2 프로젝트 멤버 추가
     */

    /**
     * 4.3 프로젝트 멤버 제거
     */
    @PostMapping("/{memberId}")
    public BaseResponse<String> deleteProjectMember(@PathVariable @NotNull(message = "요청 데이터가 잘못되었습니다.") Long memberId) {
        return new BaseResponse<>(projectMemberService.deleteMember(memberId));
    }
}
