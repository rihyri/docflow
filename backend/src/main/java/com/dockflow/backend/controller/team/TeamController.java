package com.dockflow.backend.controller.team;

import com.dockflow.backend.dto.team.*;
import com.dockflow.backend.response.ApiResponse;
import com.dockflow.backend.service.team.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    /* 전체 팀 목록 */
    @GetMapping("/all")
    public String allList(@AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam(value = "page", defaultValue = "0") int page,
                          @RequestParam(value = "size", defaultValue = "10") int size,
                          Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TeamListResponse> teamPage = teamService.getAllActiveTeams(userDetails.getUsername(), pageable);

        model.addAttribute("teams", teamPage.getContent());
        model.addAttribute("page", teamPage);
        model.addAttribute("pageTitle", "전체 팀 목록");
        model.addAttribute("isAllTeamsView", true);
        return "team/list";
    }

    /* 가입된 팀 목록 */
    @GetMapping
    public String myTeamList(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "10") int size,
                             Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TeamListResponse> teamPage = teamService.getMyTeams(userDetails.getUsername(), pageable);

        model.addAttribute("teams", teamPage.getContent());
        model.addAttribute("page", teamPage);
        model.addAttribute("pageTitle", "내 팀 목록");
        model.addAttribute("isAllTeamsView", false);

        return "team/list";
    }

    /* 팀 생성 페이지 */
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("teamCreateRequest", new TeamCreateRequest());
        return "team/create";
    }

    /* 팀 생성 처리 */
    @PostMapping("/create")
    public String createTeam(@Valid @ModelAttribute("teamCreateRequest") TeamCreateRequest request,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            return "team/create";
        }

        try {
            TeamResponse team = teamService.createTeam(request, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("message", "팀 생성이 완료되었습니다.");
            return "redirect:/teams/" + team.getTeamNo();
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "team/create";
        }
    }

    /* 팀 상세 페이지 */
    @GetMapping("/{teamNo}")
    public String teamDetail(@PathVariable("teamNo") Long teamNo, @AuthenticationPrincipal UserDetails userDetails, Model model) {
        TeamDetailResponse team = teamService.getTeamDetail(teamNo, userDetails.getUsername());
        model.addAttribute("team", team);
        return "team/detail";
    }

    /* 팀원 초대 */
    @PostMapping("/{teamNo}/invite")
    @ResponseBody
    public ApiResponse<Void> inviteMember(
            @PathVariable("teamNo") Long teamNo,
            @Valid @RequestBody TeamMemberInviteRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.error(errorMessage);
        }

        try {
            teamService.inviteMember(teamNo, userDetails.getUsername(), request);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /* 팀 정보 수정 */
    @PostMapping("/{teamNo}/modify")
    @ResponseBody
    public ApiResponse<Void> modifyTeam(
            @PathVariable("teamNo") Long teamNo,
            @Valid @RequestBody TeamModifyRequest request,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.error(errorMessage);
        }

        try {
            teamService.modifyTeam(teamNo, userDetails.getUsername(), request);
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /* 팀 비활성화 */
    @PostMapping("/{teamNo}/inactive")
    @ResponseBody
    public ApiResponse<Void> inactiveTeam(
            @PathVariable("teamNo") Long teamNo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            teamService.inActive(teamNo, userDetails.getUsername());
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
