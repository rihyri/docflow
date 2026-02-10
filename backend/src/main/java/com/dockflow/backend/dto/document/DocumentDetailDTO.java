package com.dockflow.backend.dto.document;

import com.dockflow.backend.entity.document.Document;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class DocumentDetailDTO {

    private Long documentNo;
    private String title;
    private String originalFileName;
    private String filePath;
    private Long fileSize;
    private String uploadUserName;
    private Long teamNo;
    private String teamName;
    private Document.DocumentCategory category;
    private String categoryDescription;
    private Document.DocumentStatus status;
    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String summaryText;
    private String aiModelVersion;
    private LocalDateTime summaryCreatedAt;
    private Integer summaryCount;

    private List<String> tags;

    private Boolean canResummarize;
    private Integer remainingResummaryCount;
}
