package com.dockflow.backend.dto.document;

import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentSummary;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Getter
@Builder
public class DocumentDetailResponse {

    private Long documentNo;
    private String title;
    private String originalFileName;
    private String filePath;
    private Long fileSize;
    private String uploadUserName;
    private Long uploadUserNo;
    private Long teamNo;
    private String teamName;
    private Document.DocumentCategory category;
    private String categoryDescription;
    private Document.DocumentStatus status;
    private String statusDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 요약 정보
    private String summaryText;
    private String aiModelVersion;
    private LocalDateTime summaryCreatedAt;

    public static DocumentDetailResponse from(Document document, DocumentSummary summary) {
        DocumentDetailResponseBuilder builder = DocumentDetailResponse.builder()
                .documentNo(document.getDocumentNo())
                .title(document.getTitle())
                .originalFileName(document.getOriginalFileName())
                .filePath(document.getFilePath())
                .fileSize(document.getFileSize())
                .uploadUserName(document.getUploadUser().getMemberName())
                .uploadUserNo(document.getUploadUser().getMemberNo())
                .teamNo(document.getTeam().getTeamNo())
                .teamName(document.getTeam().getTeamName())
                .category(document.getCategory())
                .categoryDescription(document.getCategory().getDescription())
                .status(document.getStatus())
                .statusDescription(document.getStatus().getDescription())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt());

        if (summary != null) {
            builder.summaryText(summary.getSummaryText())
                    .aiModelVersion(summary.getAiModelVersion())
                    .summaryCreatedAt(summary.getCreatedAt());
        }

        return builder.build();
    }
}