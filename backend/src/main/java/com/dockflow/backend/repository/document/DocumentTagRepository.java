package com.dockflow.backend.repository.document;

import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentTagRepository extends JpaRepository<DocumentTag, Long> {

    List<DocumentTag> findByDocument(Document document);

    void deleteByDocument(Document document);
}
