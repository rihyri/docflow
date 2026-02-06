package com.dockflow.backend.dto.team;

import com.dockflow.backend.entity.team.Team;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TeamResponse {

    private Long teamNo;
    private String teamName;
    private String description;
    private String ownerName;
    private LocalDateTime createdAt;

    public static TeamResponse from (Team team) {
        return TeamResponse.builder()
                .teamNo(team.getTeamNo())
                .teamName(team.getTeamName())
                .description(team.getDescription())
                .ownerName(team.getOwner().getMemberName())
                .createdAt(team.getCreatedAt())
                .build();
    }
}
