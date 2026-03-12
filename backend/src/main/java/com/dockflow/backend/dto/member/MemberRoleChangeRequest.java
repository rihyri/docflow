package com.dockflow.backend.dto.member;

import com.dockflow.backend.entity.team.TeamMember;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberRoleChangeRequest {

    @NotNull(message = "역할을 선택해주세요.")
    private TeamMember.TeamRole role;

}
