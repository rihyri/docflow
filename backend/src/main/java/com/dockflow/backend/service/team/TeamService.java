package com.dockflow.backend.service.team;

import com.dockflow.backend.dto.team.*;
import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.entity.team.Team;
import com.dockflow.backend.entity.team.TeamMember;
import com.dockflow.backend.repository.member.MemberRepository;
import com.dockflow.backend.repository.team.TeamMemberRepository;
import com.dockflow.backend.repository.team.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;


    /* 팀 생성 (생성자는 자동으로 OWNER) */
    @Transactional
    public TeamResponse createTeam(TeamCreateRequest request, String memberId) {

        Member owner = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Team team = Team.builder()
                .teamName(request.getTeamName())
                .description(request.getDescription())
                .owner(owner)
                .build();

        Team savedTeam = teamRepository.save(team);

        TeamMember ownerMember = TeamMember.builder()
                .team(savedTeam)
                .member(owner)
                .role(TeamMember.TeamRole.OWNER)
                .build();

        teamMemberRepository.save(ownerMember);

        return TeamResponse.from(savedTeam);
    }

    /* 팀 멤버 초대 */
    @Transactional
    public void inviteMember(Long teamNo, String inviterMemberId, TeamMemberInviteRequest request) {

        Team team = teamRepository.findById(teamNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        // 초대하는 사람 권한 확인 (ADMIN 이상만)
        Member inviter = memberRepository.findByMemberId(inviterMemberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        TeamMember inviterTeamMember = teamMemberRepository.findByTeamAndMember(team, inviter).orElseThrow(() -> new IllegalArgumentException("팀 멤버가 아닙니다."));

        if (!inviterTeamMember.hasPermission(TeamMember.TeamRole.ADMIN)) {
            throw new IllegalArgumentException("멤버를 초대할 권한이 없습니다.");
        }

        // 초대할 사용자 조회
        Member invitee = memberRepository.findByMemberId(request.getMemberId()).orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자가 존재하지 않습니다."));

        if (teamMemberRepository.existsByTeamAndMember(team, invitee)) {
            throw new IllegalArgumentException("이미 팀에 속한 멤버입니다.");
        }

        if (inviter.getMemberNo().equals(invitee.getMemberNo())) {
            throw new IllegalArgumentException("자기 자신을 초대할 수 없습니다.");
        }

        TeamMember newMember = TeamMember.builder()
                .team(team)
                .member(invitee)
                .role(request.getRole())
                .build();

        teamMemberRepository.save(newMember);
    }

    /* 내가 속한 팀 목록 조회 */
    public Page<TeamListResponse> getMyTeams(String memberId, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        List<TeamMember> teamMembers = teamMemberRepository.findTeamsByMember(member);
        List<Long> teamIds = teamMembers.stream()
                .map(tm -> tm.getTeam().getTeamNo())
                .toList();

        if (teamIds.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Team> teamPage = teamRepository.findByTeamNoInAndIsActiveTrue(teamIds, pageable);

        Map<Long, TeamMember.TeamRole> roleMap = teamMembers.stream()
                .collect(Collectors.toMap(
                        tm -> tm.getTeam().getTeamNo(),
                        TeamMember::getRole
                ));

        return teamPage.map(team -> {
           int memberCount = teamMemberRepository.countByTeam(team);
           return TeamListResponse.builder()
                   .teamNo(team.getTeamNo())
                   .teamName(team.getTeamName())
                   .description(team.getDescription())
                   .ownerName(team.getOwner().getMemberName())
                   .memberCount(memberCount)
                   .myRole(roleMap.get(team.getTeamNo()))
                   .createdAt(team.getCreatedAt())
                   .build();
        });
    }

    /* 전체 팀 목록 조회 */
    public Page<TeamListResponse> getAllActiveTeams(String memberId, Pageable pageable) {
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Page<Team> teamPage = teamRepository.findByIsActiveTrue(pageable);

        List<TeamMember> myTeamMembers = teamMemberRepository.findTeamsByMember(member);
        Map<Long, TeamMember.TeamRole> myTeamRoleMap = myTeamMembers.stream()
                .collect(Collectors.toMap(
                    tm -> tm.getTeam().getTeamNo(),
                        TeamMember::getRole
                ));

        return teamPage.map(team -> {
            int memberCount = teamMemberRepository.countByTeam(team);
            TeamMember.TeamRole myRole = myTeamRoleMap.get(team.getTeamNo());
            return TeamListResponse.builder()
                    .teamNo(team.getTeamNo())
                    .teamName(team.getTeamName())
                    .description(team.getDescription())
                    .ownerName(team.getOwner().getMemberName())
                    .myRole(myRole)
                    .memberCount(memberCount)
                    .createdAt(team.getCreatedAt())
                    .build();
        });
    }

    /* 팀 상세 조회 (멤버 목록 포함) */
    public TeamDetailResponse getTeamDetail(Long teamNo, String memberId) {
        Team team = teamRepository.findById(teamNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 팀 멤버만 조회 가능
        TeamMember teamMember = teamMemberRepository.findByTeamAndMember(team, member).orElseThrow(() -> new IllegalArgumentException("팀 멤버가 아닙니다."));

        List<TeamMember> members = teamMemberRepository.findByTeam(team);

        return TeamDetailResponse.from(team, members, teamMember.getRole());
    }
}
