package com.dockflow.backend.dto.team;

import com.dockflow.backend.entity.team.Team;
import com.dockflow.backend.entity.team.TeamMember;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamListResponse {

    private Long teamNo;
    private String teamName;
    private String description;
    private String ownerName;
    private Integer memberCount;
    private TeamMember.TeamRole myRole;
    private LocalDateTime createdAt;


    public static TeamListResponse from (Team team, TeamMember.TeamRole role) {
        return TeamListResponse.builder()
                .teamNo(team.getTeamNo())
                .teamName(team.getTeamName())
                .description(team.getDescription())
                .myRole(role)
                .createdAt(team.getCreatedAt())
                .build();
    }

    public boolean isJoined() {
        return myRole != null;
    }
}
