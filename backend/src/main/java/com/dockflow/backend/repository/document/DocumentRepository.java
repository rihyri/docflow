package com.dockflow.backend.repository.document;

import com.dockflow.backend.entity.document.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    // 팀별 문서 목록 (isActive ture)
    Page<Document> findByTeamTeamNoAndIsActiveTrueOrderByCreatedAtDesc(Long teamNo, Pageable pageable);

    // 문서 search
    @Query("""
        SELECT d FROM Document d
            JOIN d.uploadUser u
        WHERE d.team.teamNo = :teamNo
            AND d.isActive = true
            AND (:category IS NULL or d.category = :category)
            AND (:startDate IS NULL or d.createdAt >= :startDate)
            AND (:endDate IS NULL or d.createdAt <= :endDate)
            AND (
                :searchType IS NULL OR :searchKeyword IS NULL OR :searchKeyword = '' OR
                (:searchType = 'title' AND d.title LIKE %:searchKeyword%) OR
                (:searchType = 'originalFileName' AND d.originalFileName LIKE %:searchKeyword%) OR
                (:searchType = 'uploadUserName' AND u.memberName LIKE %:searchKeyword%)
            )
            ORDER BY d.createdAt DESC    
    """)
    Page<Document> searchDocuments(
            @Param("teamNo") Long teamNo,
            @Param("category") Document.DocumentCategory category,
            @Param("searchType") String searchType,
            @Param("searchKeyword") String searchKeyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );


    // 특정 문서 조회 (isActive true)
    Optional<Document> findByDocumentNoAndIsActiveTrue(Long documentNo);

    // 팀별 문서 개수
    long countByTeamTeamNoAndIsActiveTrue(Long teamNo);

    // 카테고리별 문서 조회
    Page<Document> findByTeamTeamNoAndCategoryAndIsActiveTrueOrderByCreatedAtDesc(Long teamNo, Document.DocumentCategory category, Pageable pageable);

    // 제목 검색
    Page<Document> findByTeamTeamNoAndTitleContainingAndIsActiveTrueOrderByCreatedAtDesc(Long teamNo, String keyword, Pageable pageable);
}
