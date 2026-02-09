package com.dockflow.backend.dto.document;

import com.dockflow.backend.entity.document.Document;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DocumentResponse {

    private Long documentNo;
    private String title;
    private String originalFileName;
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

    public static DocumentResponse from(Document document) {
        return DocumentResponse.builder()
                .documentNo(document.getDocumentNo())
                .title(document.getTitle())
                .originalFileName(document.getOriginalFileName())
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
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
