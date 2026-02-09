package com.dockflow.backend.entity.document;

import com.dockflow.backend.entity.member.Member;
import com.dockflow.backend.entity.team.Team;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_no")
    private Long documentNo;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFileName;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upload_user_no", nullable = false)
    private Member uploadUser;

    @ManyToOne(fetch =FetchType.LAZY)
    @JoinColumn(name = "team_no", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    @Builder.Default
    private DocumentCategory category = DocumentCategory.ETC;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.PROCESSING;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum DocumentCategory {
        MEETING("회의록"),
        PLAN("기획서"),
        REPORT("보고서"),
        ETC("기타");

        private final String description;

        DocumentCategory(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum DocumentStatus {
        PROCESSING("처리중"),
        COMPLETED("완료"),
        FAILED("실패");

        private final String description;

        DocumentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public void updateDocumentInfo(String title, DocumentCategory category) {
        this.title = title;
        this.category = category;
    }

    public void updateStatus(DocumentStatus status) {
        this.status = status;
    }

    public void deactivate() {
        this.isActive = false;
    }

}
