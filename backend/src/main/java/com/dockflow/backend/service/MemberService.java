package com.dockflow.backend.service;

import com.dockflow.backend.dto.member.MemberJoinDTO;
import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    /* 아이디 중복 체크 */
    public boolean checkMemberIdDuplicate(String memberId) {
        boolean exists = memberRepository.existsByMemberId(memberId);
        log.info("아이디 중복 체크 - memberId: {}, exists: {}", memberId, exists);
        return exists;
    }

    /* 이메일 중복 체크 */
    public boolean checkEmailDuplicate(String email) {
        boolean exists = memberRepository.existsByEmail(email);
        log.info("이메일 중복 체크 - email: {}, exists: {}", email, exists);
        return exists;
    }

    /* 회원가입 */
    @Transactional
    public Long join(MemberJoinDTO joinDTO) {

        // 1. 비밀번호 일치 여부 확인
        if (!joinDTO.isPasswordMatch()) {
            log.error("회원가입 실패 - 비밀번호 불일치");
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 아이디 중복 체크
        if (memberRepository.existsByMemberId(joinDTO.getMemberId())) {
            log.error("회원가입 실패 - 아이디 중복: {}", joinDTO.getMemberId());
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }

        // 3. 이메일 중복 체크
        String fullEmail = joinDTO.getFullEmail();
        if (memberRepository.existsByEmail(fullEmail)) {
            log.error("회원가입 실패 - 이메일 중복: {}", fullEmail);
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        // 4. 회원 정보 저장
        Member member = Member.builder()
                .memberId(joinDTO.getMemberId())
                .memberPw(passwordEncoder.encode(joinDTO.getMemberPw()))
                .memberName(joinDTO.getMemberName())
                .email(fullEmail)
                .role(Member.Role.USER)
                .isActive(true)
                .build();

        Member savedMember = memberRepository.save(member);
        log.info("회원가입 성공 - memberNo: {}, memberId: {}", savedMember.getMemberNo(), savedMember.getMemberId());

        return savedMember.getMemberNo();
    }
}
