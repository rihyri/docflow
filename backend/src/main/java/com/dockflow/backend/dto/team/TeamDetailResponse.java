package com.dockflow.backend.dto.team;

import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.entity.team.Team;
import com.dockflow.backend.entity.team.TeamMember;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class TeamDetailResponse {

    private Long teamNo;
    private String teamName;
    private String description;
    private String ownerName;
    private TeamMember.TeamRole myRole;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TeamMemberInfo> members;

    @Getter
    @Builder
    public static class TeamMemberInfo {
        private Long memberNo;
        private String memberId;
        private String memberName;
        private String email;
        private TeamMember.TeamRole role;
        private LocalDateTime joinedAt;

        public static TeamMemberInfo from(TeamMember teamMember) {
            Member member = teamMember.getMember();
            return TeamMemberInfo.builder()
                    .memberNo(member.getMemberNo())
                    .memberId(member.getMemberId())
                    .memberName(member.getMemberName())
                    .email(member.getEmail())
                    .role(teamMember.getRole())
                    .joinedAt(teamMember.getJoinedAt())
                    .build();
        }
    }

    public static TeamDetailResponse from(Team team, List<TeamMember> members, TeamMember.TeamRole myRole) {
        return TeamDetailResponse.builder()
                .teamNo(team.getTeamNo())
                .teamName(team.getTeamName())
                .description(team.getDescription())
                .ownerName(team.getOwner().getMemberName())
                .myRole(myRole)
                .createdAt(team.getCreatedAt())
                .updatedAt(team.getUpdatedAt())
                .members(members.stream()
                        .map(TeamMemberInfo::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
