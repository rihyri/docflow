package com.dockflow.backend.dto.team;

import com.dockflow.backend.entity.team.TeamMember;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberInviteRequest {

    @NotBlank(message = "멤버 아이디는 필수입니다.")
    private String memberId;

    @NotNull(message = "역할은 필수입니다.")
    private TeamMember.TeamRole role;
}
