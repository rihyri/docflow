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
public class MemberIdCheckDTO {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String memberId;

}
