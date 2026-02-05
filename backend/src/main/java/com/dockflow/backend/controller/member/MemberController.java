package com.dockflow.backend.controller.member;

import com.dockflow.backend.dto.member.MemberEmailCheckDTO;
import com.dockflow.backend.dto.member.MemberIdCheckDTO;
import com.dockflow.backend.dto.member.MemberJoinDTO;
import com.dockflow.backend.response.ApiResponse;
import com.dockflow.backend.service.member.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    /* 로그인 */
    @GetMapping("/login")
    public String login() {
        return "member/login";
    }

    /* 회원가입 */
    @GetMapping("/join")
    public String join() {
        return "member/join";
    }

    /* 아이디 중복 체크 API */
    @PostMapping("/api/member/check-id")
    @ResponseBody
    public ApiResponse<Boolean> checkMemberId(@Valid @RequestBody MemberIdCheckDTO dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.error(errorMessage);
        }

        boolean isDuplicate = memberService.checkMemberIdDuplicate(dto.getMemberId());

        if (isDuplicate) {
            return ApiResponse.error("이미 사용중인 아이디입니다.");
        }

        return ApiResponse.success("사용 가능한 아이디입니다.", false);
    }

    /* 이메일 중복 체크 API */
    @PostMapping("/api/member/check-email")
    @ResponseBody
    public ApiResponse<Boolean> checkEmail(@Valid @RequestBody MemberEmailCheckDTO dto, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            return ApiResponse.error(errorMessage);
        }

        String fullEmail = dto.getFullEmail();
        boolean isDuplicate = memberService.checkEmailDuplicate(fullEmail);

        if (isDuplicate) {
            return ApiResponse.error("이미 사용중인 이메일입니다.");
        }

        return ApiResponse.success("사용 가능한 이메일입니다.", false);
    }

    /* 회원가입 처리 */
    @PostMapping("/join")
    public String joinProcess(@Valid @ModelAttribute MemberJoinDTO joinDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/join";
        }

        try {
            memberService.join(joinDTO);
            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            log.error("회원가입 실패: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/join";
        } catch (Exception e) {
            log.error("회원가입 중 오류 발생", e);
            redirectAttributes.addFlashAttribute("error", "회원가입 중 오류가 발생했습니다.");
            return "redirect:/join";
        }
    }
}
