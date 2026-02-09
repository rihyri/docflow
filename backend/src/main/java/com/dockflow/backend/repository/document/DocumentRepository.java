package com.dockflow.backend.repository.document;

import com.dockflow.backend.entity.document.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 팀별 문서 목록 (isActive ture)
    Page<Document> findByTeamTeamNoAndIsActiveTrueOrderByCreatedAtDesc(Long teamNo, Pageable pageable);

    // 특정 문서 조회 (isActive true)
    Optional<Document> findByDocumentNoAndIsActiveTrue(Long documentNo);

    // 팀별 문서 개수
    long countByTeamTeamNoAndIsActiveTrue(Long teamNo);

    // 카테고리별 문서 조회
    Page<Document> findByTeamTeamNoAndCategoryAndIsActiveTrueOrderByCreatedAtDesc(Long teamNo, Document.DocumentCategory category, Pageable pageable);

    // 제목 검색
    Page<Document> findByTeamTeamNoAndTitleContainingAndIsActiveTrueOrderByCreatedAtDesc(Long teamNo, String keyword, Pageable pageable);
}
