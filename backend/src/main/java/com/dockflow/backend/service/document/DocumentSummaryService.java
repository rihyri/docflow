package com.dockflow.backend.service.document;

import com.dockflow.backend.claude.ClaudeApiService;
import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.entity.document.DocumentSummary;
import com.dockflow.backend.entity.document.DocumentTag;
import com.dockflow.backend.etc.FileTextExtractor;
import com.dockflow.backend.repository.document.DocumentRepository;
import com.dockflow.backend.repository.document.DocumentSummaryRepository;
import com.dockflow.backend.repository.document.DocumentTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentSummaryService {

    private final DocumentRepository documentRepository;
    private final DocumentSummaryRepository summaryRepository;
    private final DocumentTagRepository tagRepository;
    private final ClaudeApiService claudeApiService;
    private final FileTextExtractor fileTextExtractor;

    @Value("${claude.api.model}")
    private String aiModelVersion;

    /* 문서 업로드시 자동 요약 (비동기) */
    @Async
    @Transactional
    public void summarizeDocumentAsync(Long documentNo) {

        log.info("문서 요약 시작: documentNo={}", documentNo);

        try {
            Document document = documentRepository.findById(documentNo).orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

            // 1. 파일에서 텍스트 추출
            String text = fileTextExtractor.extractText(document.getFilePath());

            // 2. 텍스트 길이 제한 (약 15,000자)
            text = fileTextExtractor.limitTextLength(text, 15000);

            // 3. Claude API 호출
            ClaudeApiService.SummaryResult result = claudeApiService.summarizeDocument(text, document.getTitle());

            // 4. 요약 저장
            DocumentSummary summary = DocumentSummary.builder()
                    .document(document)
                    .summaryText(result.summary())
                    .aiModelVersion(aiModelVersion)
                    .summaryCount(1)
                    .lastSummarizedAt(LocalDateTime.now())
                    .build();
            summaryRepository.save(summary);

            // 5. 태그 저장
            for (String tagName : result.tags()) {
                DocumentTag tag = DocumentTag.builder()
                        .document(document)
                        .tagName(tagName)
                        .build();
                tagRepository.save(tag);
            }

            // 6. 문서 상태 업데이트
            document.updateStatus(Document.DocumentStatus.COMPLETED);
            documentRepository.save(document);

            log.info("문서 요약 완료: documentNo={}", documentNo);

        } catch (Exception e) {
            log.error("문서 요약 실패: documentNo={}", documentNo, e);

            documentRepository.findById(documentNo).ifPresent(doc -> {
                doc.updateStatus(Document.DocumentStatus.FAILED);
                documentRepository.save(doc);
            });
        }
    }

    /* 수동 재요약 */
    @Transactional
    public void resummarizeDocument(Long documentNo) {

        log.info("문서 재요약 시작: documentNo={}", documentNo);

        Document document = documentRepository.findById(documentNo).orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

        DocumentSummary existingSummary = summaryRepository.findByDocument(document).orElseThrow(() -> new IllegalArgumentException("요약 정보를 찾을 수 없습니다."));

        // 재요약 가능 여부 체크
        if (!existingSummary.canResummarize()) {
            throw new IllegalArgumentException("이번 달 재요약 횟수를 모두 사용했습니다. (월 3회)");
        }

        try {
            // 1. 파일에서 텍스트 추출
            String text = fileTextExtractor.extractText(document.getFilePath());
            text = fileTextExtractor.limitTextLength(text, 15000);

            // 2. Claude API 호출
            ClaudeApiService.SummaryResult result = claudeApiService.summarizeDocument(text, document.getTitle());

            // 3. 기존 요약 업데이트
            existingSummary.updateSummary(result.summary(), aiModelVersion);
            summaryRepository.save(existingSummary);

            // 4. 기존 태그 삭제 후 새 태그 저장
            tagRepository.deleteByDocument(document);
            for (String tagName : result.tags()) {
                DocumentTag tag = DocumentTag.builder()
                        .document(document)
                        .tagName(tagName)
                        .build();
                tagRepository.save(tag);
            }

            log.info("문서 재요약 완료: documentNo={}", documentNo);

        } catch (Exception e) {
            log.error("문서 재요약 실패: documentNo={}", documentNo, e);
            throw new RuntimeException("재요약에 실패했습니다.", e);
        }
    }

    /* 재요약 가능 여부 조회 */
    @Transactional(readOnly = true)
    public boolean canResummarize(Long documentNo) {
        Document document = documentRepository.findById(documentNo).orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

        return summaryRepository.findByDocument(document)
                .map(DocumentSummary::canResummarize)
                .orElse(false);
    }

    /* 남은 재요약 횟수 조회 */
    @Transactional(readOnly = true)
    public int getRemainingResummaryCount(Long documentNo) {
        Document document = documentRepository.findById(documentNo).orElseThrow(() -> new IllegalArgumentException("문서를 찾을 수 없습니다."));

        return summaryRepository.findByDocument(document)
                .map(summary -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime currentMonthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

                    if (summary.getLastSummarizedAt() == null || summary.getLastSummarizedAt().isBefore(currentMonthStart)) {
                        return 3;
                    }
                    return Math.max(0, 3 - summary.getSummaryCount());
                })
                .orElse(0);
    }
}
