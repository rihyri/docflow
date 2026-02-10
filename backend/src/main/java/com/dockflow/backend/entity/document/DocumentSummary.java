package com.dockflow.backend.entity.document;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENT_SUMMARY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DocumentSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_no")
    private Long summaryNo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_no", nullable = false)
    private Document document;

    @Column(name = "summary_text", columnDefinition = "TEXT", nullable = false)
    private String summaryText;

    @Column(name = "ai_model_version", length = 50)
    private String aiModelVersion;

    @Column(name = "summary_count", nullable = false)
    @Builder.Default
    private Integer summaryCount = 1;

    @Column(name = "list_summarized_at")
    private LocalDateTime lastSummarizedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 재요약 가능 여부 체크 (월 3회 제한)
    public boolean canResummarize() {

        if (lastSummarizedAt == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        if (lastSummarizedAt.isAfter(currentMonthStart)) {
            return summaryCount < 3;
        }

        return true;
    }

    // 재요약 실행
    public void updateSummary(String summaryText, String aiModelVersion) {
        this.summaryText = summaryText;
        this.aiModelVersion = aiModelVersion;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime currentMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        if (lastSummarizedAt == null || lastSummarizedAt.isBefore(currentMonthStart)) {
            this.summaryCount = 1;
        } else {
            this.summaryCount++;
        }

        this.lastSummarizedAt = now;
    }
}
