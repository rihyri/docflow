package com.dockflow.backend.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEmailCheckDTO {

    @NotBlank(message = "이메일을 입력해주세요.")
    private String email1;

    @NotBlank(message = "이메일 도메인을 입력해주세요.")
    private String email2;

    public String getFullEmail() {
        return email1 + "@" + email2;
    }
}

