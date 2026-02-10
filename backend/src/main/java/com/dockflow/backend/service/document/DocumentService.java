package com.dockflow.backend.service.document;

import com.dockflow.backend.dto.document.DocumentCreateRequest;
import com.dockflow.backend.dto.document.DocumentDetailResponse;
import com.dockflow.backend.dto.document.DocumentResponse;
import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentSummary;
import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.entity.team.Team;
import com.dockflow.backend.entity.team.TeamMember;
import com.dockflow.backend.repository.document.DocumentRepository;
import com.dockflow.backend.repository.document.DocumentSummaryRepository;
import com.dockflow.backend.repository.member.MemberRepository;
import com.dockflow.backend.repository.team.TeamMemberRepository;
import com.dockflow.backend.repository.team.TeamRepository;
import com.dockflow.backend.service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentSummaryRepository documentSummaryRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;

    /* 문서 업로드 */
    @Transactional
    public DocumentResponse uploadDocument(
            MultipartFile file,
            DocumentCreateRequest request,
            String memberId
    ) {

        // 1. 멤버 조회
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 팀 조회 및 권한 확인
        Team team = teamRepository.findById(request.getTeamNo())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        // 3. 팀 멤버인지 확인
        TeamMember teamMember = teamMemberRepository.findByTeamAndMember(team, member)
                .orElseThrow(() -> new IllegalArgumentException("팀 멤버만 문서를 업로드할 수 있습니다."));

        // VIEWER는 업로드 불가
        if (teamMember.getRole() == TeamMember.TeamRole.VIEWER) {
            throw new IllegalArgumentException("조회 권한만 있는 멤버는 문서를 업로드할 수 없습니다.");
        }

        // 4. 파일 저장
        String filePath = fileStorageService.storeFile(file, team.getTeamNo());

        // 5. 문서 엔티티 생성 및 저장
        Document document = Document.builder()
                .title(request.getTitle())
                .originalFileName(file.getOriginalFilename())
                .filePath(filePath)
                .fileSize(file.getSize())
                .uploadUser(member)
                .team(team)
                .category(request.getCategory())
                .status(Document.DocumentStatus.PROCESSING)
                .build();

        Document savedDocument = documentRepository.save(document);

        log.info("문서 업로드 완료: documentNo={}, title={}", savedDocument.getDocumentNo(), savedDocument.getTitle());

        return DocumentResponse.from(savedDocument);
    }

    /* 팀별 문서 목록 조회 */
    public Page<DocumentResponse> getTeamDocuments(Long teamNo, String memberId, Pageable pageable) {

        // 1. 회원 조회
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 팀 조회
        Team team = teamRepository.findById(teamNo)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        // 3. 팀 멤버 권한 확인
        teamMemberRepository.findByTeamAndMember(team, member).orElseThrow(() -> new IllegalArgumentException("팀 멤버만 문서를 조회할 수 있습니다."));

        // 4. 문서 목록 조회
        Page<Document> documentPage = documentRepository.findByTeamTeamNoAndIsActiveTrueOrderByCreatedAtDesc(teamNo, pageable);

        return documentPage.map(DocumentResponse::from);
    }

    /* 문서 상세 조회 */
    public DocumentDetailResponse getDocumentDetail(Long documentNo, String memberId) {

        // 1. 회원 조회
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 문서 조회
        Document document = documentRepository.findByDocumentNoAndIsActiveTrue(documentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        // 3. 팀 멤버 권한 확인
        teamMemberRepository.findByTeamAndMember(document.getTeam(), member).orElseThrow(() -> new IllegalArgumentException("팀 멤버만 문서를 조회할 수 있습니다."));

        // 4. 요약 조회 (존재시)
        DocumentSummary summary = documentSummaryRepository.findByDocumentDocumentNo(documentNo).orElse(null);

        return DocumentDetailResponse.from(document, summary);
    }
}
