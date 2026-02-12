package com.dockflow.backend.repository.document.comment;

import com.dockflow.backend.entity.document.DocumentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentCommentRepository extends JpaRepository<DocumentComment, Long> {

    List<DocumentComment> findByDocument_DocumentNoOrderByCreatedAtAsc(Long documentNo);

    int countByDocument_DocumentNo(Long documentNo);
}
