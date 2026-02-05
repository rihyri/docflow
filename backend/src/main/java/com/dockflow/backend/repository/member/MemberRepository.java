package com.dockflow.backend.repository.member;

import com.dockflow.backend.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 아이디 중복 체크
    boolean existsByMemberId(String memberId);

    // 이메일 중복 체크
    boolean existsByEmail(String email);

    // 아이디로 회원 조회
    Optional<Member> findByMemberId(String memberId);

    // 이메일로 회원 조회
    Optional<Member> findByEmail(String email);
}
