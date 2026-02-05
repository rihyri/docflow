package com.dockflow.backend.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberJoinDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 30, message = "아이디는 4자 이상 30자 이하로 입력해주세요.")
    private String memberId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 8자 이상 입력해주세요.")
    private String memberPw;

    @NotBlank(message = "비밀번호 확인을 입력해주세요.")
    private String memberPwCheck;

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(max = 20, message = "이름은 20자 이하로 입력해주세요.")
    private String memberName;

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email1;

    @NotBlank(message = "이메일 도메인을 입력해주세요.")
    private String email2;

    public String getFullEmail() {
        return email1 + "@" + email2;
    }

    public boolean isPasswordMatch() {
        return memberPw != null && memberPw.equals(memberPwCheck);
    }
}