package com.dockflow.backend.service.document;

import com.dockflow.backend.dto.document.*;
import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentSummary;
import com.dockflow.backend.entity.document.DocumentTag;
import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.entity.team.Team;
import com.dockflow.backend.entity.team.TeamMember;
import com.dockflow.backend.repository.document.DocumentRepository;
import com.dockflow.backend.repository.document.DocumentSummaryRepository;
import com.dockflow.backend.repository.document.DocumentTagRepository;
import com.dockflow.backend.repository.member.MemberRepository;
import com.dockflow.backend.repository.team.TeamMemberRepository;
import com.dockflow.backend.repository.team.TeamRepository;
import com.dockflow.backend.service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentSummaryRepository summaryRepository;
    private final DocumentTagRepository tagRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MemberRepository memberRepository;
    private final FileStorageService fileStorageService;
    private final DocumentSummaryService documentSummaryService;

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

        // 비동기로 AI 요약 실행
        documentSummaryService.summarizeDocumentAsync(savedDocument.getDocumentNo());

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

    /* 팀별 문서 목록 검색 */
    public Page<DocumentResponse> searchTeamDocuments(
            Long teamNo, String memberId, Document.DocumentCategory category,
            String searchType, String searchKeyword,
            LocalDateTime startDate, LocalDateTime endDate,
            Pageable pageable) {

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Team team = teamRepository.findById(teamNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        teamMemberRepository.findByTeamAndMember(team, member).orElseThrow(() -> new IllegalArgumentException("팀 멤버만 문서를 조회할 수 있습니다."));

        Page<Document> documentPage = documentRepository.searchDocuments(teamNo, category, searchType, searchKeyword, startDate, endDate, pageable);

        return documentPage.map(DocumentResponse::from);
    }

    /* 문서 상세 조회 */
    public DocumentDetailDTO getDocumentDetail(Long documentNo, String memberId) {

        // 1. 회원 조회
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 문서 조회
        Document document = documentRepository.findByDocumentNoAndIsActiveTrue(documentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        // 3. 팀 멤버 권한 확인
        teamMemberRepository.findByTeamAndMember(document.getTeam(), member).orElseThrow(() -> new IllegalArgumentException("팀 멤버만 문서를 조회할 수 있습니다."));

        // 4. 요약 조회 (존재시)
        var summaryOpt = summaryRepository.findByDocument(document);

        List<String> tags = tagRepository.findByDocument(document)
                .stream()
                .map(DocumentTag::getTagName)
                .toList();

        return DocumentDetailDTO.builder()
                .documentNo(document.getDocumentNo())
                .title(document.getTitle())
                .originalFileName(document.getOriginalFileName())
                .filePath(document.getFilePath())
                .fileSize(document.getFileSize())
                .uploadUserName(document.getUploadUser().getMemberName())
                .teamNo(document.getTeam().getTeamNo())
                .teamName(document.getTeam().getTeamName())
                .category(document.getCategory())
                .categoryDescription(document.getCategory().getDescription())
                .status(document.getStatus())
                .statusDescription(document.getStatus().getDescription())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .summaryText(summaryOpt.map(s -> s.getSummaryText()).orElse(null))
                .aiModelVersion(summaryOpt.map(s -> s.getAiModelVersion()).orElse(null))
                .summaryCreatedAt(summaryOpt.map(s -> s.getCreatedAt()).orElse(null))
                .summaryCount(summaryOpt.map(s -> s.getSummaryCount()).orElse(0))
                .tags(tags)
                .canResummarize(summaryOpt.map(s -> s.canResummarize()).orElse(false))
                .remainingResummaryCount(documentSummaryService.getRemainingResummaryCount(documentNo))
                .build();
    }

    /* 문서 정보 수정 */
    @Transactional
    public DocumentResponse updateDocument(Long documentNo, DocumentUpdateRequest request, String memberId) {

        // 1. 회원 조회
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 문서 조회
        Document document = documentRepository.findByDocumentNoAndIsActiveTrue(documentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        // 3. 권한 확인 (OWNER, ADMIN, 작성자만 수정 가능)
        TeamMember teamMember = teamMemberRepository.findByTeamAndMember(document.getTeam(), member).orElseThrow(() -> new IllegalArgumentException("팀 멤버만 문서를 수정할 수 있습니다."));

        boolean isOwnerOrAdmin = teamMember.getRole() == TeamMember.TeamRole.OWNER || teamMember.getRole() == TeamMember.TeamRole.ADMIN;
        boolean isAuthor = document.getUploadUser().getMemberNo().equals(member.getMemberNo());

        if (!isOwnerOrAdmin && !isAuthor) {
            throw new IllegalArgumentException("문서 수정 권한이 없습니다.");
        }

        // 4. 문서 정보 업데이트
        document.updateDocumentInfo(request.getTitle(), request.getCategory());

        log.info("문서 수정 완료: documentNo{}, title={}", documentNo, request.getTitle());

        return DocumentResponse.from(document);
    }

    /* 문서 삭제 (비활성화) */
    @Transactional
    public void deleteDocument(Long documentNo, String memberId) {

        // 1. 회원 조회
        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. 문서 조회
        Document document = documentRepository.findByDocumentNoAndIsActiveTrue(documentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        // 3. 권한 확인 (OWNER, ADMIN, 작성자만 삭제 가능)
        TeamMember teamMember = teamMemberRepository.findByTeamAndMember(document.getTeam(), member).orElseThrow(() -> new IllegalArgumentException("팀 멤버만 문서를 삭제할 수 있습니다."));

        boolean isOwnerOrAdmin = teamMember.getRole() == TeamMember.TeamRole.OWNER || teamMember.getRole() == TeamMember.TeamRole.ADMIN;
        boolean isAuthor = document.getUploadUser().getMemberNo().equals(member.getMemberNo());

        if (!isOwnerOrAdmin && !isAuthor) {
            throw new IllegalArgumentException("문서 삭제 권한이 없습니다.");
        }

        // 4. 문서 비활성화
        document.deactivate();

        log.info("문서 삭제 완료: documentNo = {}, title = {}", documentNo, document.getTitle());
    }

    /* 관련 문서 추천 (같은 팀, 같은 태그 기반) */
    public List<RelatedDocumentDTO> getRelatedDocuments(Long documentNo, int limit) {

        // 1. 현재 문서 조회
        Document document = documentRepository.findByDocumentNoAndIsActiveTrue(documentNo).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문서입니다."));

        // 2. 현재 문서의 태그 목록 가져오기
        List<String> tagNames = tagRepository.findByDocument(document)
                .stream()
                .map(DocumentTag::getTagName)
                .toList();

        // 태그가 없으면 빈 리스트 반환
        if (tagNames.isEmpty()) {
            return List.of();
        }

        // 3. 같은 팀에서 같은 태그를 가진 문서 찾기
        List<Object[]> results = tagRepository.findRelatedDocumentsByTagsAndTeam(
                tagNames,
                document.getTeam(),
                documentNo,
                PageRequest.of(0, limit)
        );

        return results.stream()
                .map(result -> {
                    Document relatedDoc = (Document) result[0];
                    Long matchCount = (Long) result[1];

                    return RelatedDocumentDTO.builder()
                            .documentNo(relatedDoc.getDocumentNo())
                            .title(relatedDoc.getTitle())
                            .categoryDescription(relatedDoc.getCategory().getDescription())
                            .matchingTagCount(matchCount.intValue())
                            .build();
                })
                .toList();
    }
}
