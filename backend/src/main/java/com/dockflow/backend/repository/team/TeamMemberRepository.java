package com.dockflow.backend.repository.team;

import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.entity.team.Team;
import com.dockflow.backend.entity.team.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    // 팀 멤버 목록
    List<TeamMember> findByTeam(Team team);

    int countByTeam(Team team);

    // 특정 멤버의 속한 팀 목록
    @Query("SELECT tm FROM TeamMember tm JOIN FETCH tm.team t"
            + " WHERE tm.member = :member AND t.isActive = true")
    List<TeamMember> findTeamsByMember(@Param("member") Member member);

    // 중복 가입 체크
    boolean existsByTeamAndMember(Team team, Member member);

    // 권환 확인
    Optional<TeamMember> findByTeamAndMember(Team team, Member member);
}
