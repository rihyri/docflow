package com.dockflow.backend.repository.document;

import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentTag;
import com.dockflow.backend.entity.team.Team;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentTagRepository extends JpaRepository<DocumentTag, Long> {

    List<DocumentTag> findByDocument(Document document);

    void deleteByDocument(Document document);

    // 특정 태그명들과 팀으로 문서 찾기
    @Query("SELECT dt.document, COUNT(dt) as matchCount " +
            "FROM DocumentTag dt " +
            "WHERE dt.tagName IN :tagNames " +
            "AND dt.document.team = :team " +
            "AND dt.document.isActive = true " +
            "AND dt.document.documentNo != :excludeDocumentNo " +
            "GROUP BY dt.document " +
            "ORDER BY matchCount DESC")
    List<Object[]> findRelatedDocumentsByTagsAndTeam(
            @Param("tagNames") List<String> tagNames,
            @Param("team")Team team,
            @Param("excludeDocumentNo") Long excludeDocumentNo,
            Pageable pageable
    );
}
