package com.dockflow.backend.repository.document;

import com.dockflow.backend.entity.document.DocumentSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentSummaryRepository extends JpaRepository<DocumentSummary, Long> {

    Optional<DocumentSummary> findByDocumentDocumentNo(Long documentNo);

    boolean existsByDocumentDocumentNo(Long documentNo);
}
