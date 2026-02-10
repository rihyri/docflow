package com.dockflow.backend.repository.document;

import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentSummary;
import com.dockflow.backend.entity.document.DocumentTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentSummaryRepository extends JpaRepository<DocumentSummary, Long> {

    Optional<DocumentSummary> findByDocumentDocumentNo(Long documentNo);

    Optional<DocumentSummary> findByDocument(Document document);

    boolean existsByDocumentDocumentNo(Long documentNo);

    void deleteByDocument(Document document);
}
