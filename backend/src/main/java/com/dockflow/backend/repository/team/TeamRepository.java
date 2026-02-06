package com.dockflow.backend.repository.team;

import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.entity.team.Team;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    // 활동중인 team 조회
    Page<Team> findByIsActiveTrue(Pageable pageable);

    // 소유자로 team 조회
    Page<Team> findByOwnerAndIsActiveTrue(Member owner, Pageable pageable);

    @Query("SELECT t FROM Team t WHERE t.teamNo " +
            "IN:teamIds AND t.isActive = true")
    Page<Team> findByTeamNoInAndIsActiveTrue(@Param("teamIds") List<Long> teamIds, Pageable pageable);

    // 팀명으로 조회
    @Query("SELECT t FROM Team t WHERE t.teamName"
            + " LIKE %:keyword% AND t.isActive = true")
    Page<Team> searchByTeamName(@Param("keyword") String keyword, Pageable pageable);
}
