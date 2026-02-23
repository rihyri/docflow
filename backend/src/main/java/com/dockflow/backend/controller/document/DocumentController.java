package com.dockflow.backend.controller.document;

import com.dockflow.backend.dto.document.*;
import com.dockflow.backend.dto.document.comment.DocumentCommentDTO;
import com.dockflow.backend.entity.document.Document;
import com.dockflow.backend.response.ApiResponse;
import com.dockflow.backend.service.document.DocumentService;
import com.dockflow.backend.service.document.DocumentSummaryService;
import com.dockflow.backend.service.document.comment.DocumentCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentSummaryService documentSummaryService;
    private final DocumentCommentService documentCommentService;


    /* 문서 업로드 페이지 */
    @GetMapping("/upload")
    public String uploadPage(@RequestParam(value = "teamNo") Long teamNo, Model model) {
        model.addAttribute("teamNo", teamNo);
        model.addAttribute("categories", Document.DocumentCategory.values());
        return "/document/upload";
    }

    /* 문서 업로드 처리 */
    @PostMapping("/upload")
    public String uploadDocument(
            @RequestParam(value = "file") MultipartFile file,
            @Valid @ModelAttribute DocumentCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("파일을 선택해주세요.");
            }

            documentService.uploadDocument(file, request, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("message", "문서가 업로드되었습니다.");

            return "redirect:/documents/list?teamNo=" + request.getTeamNo();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/documents/upload?teamNo=" + request.getTeamNo();
        }
    }

    /* 팀별 문서 목록 */
    @GetMapping("/list")
    public String documentList(
            @RequestParam(value = "teamNo") Long teamNo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "category", required = false) String categoryStr,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        // 카테고리 변환
        Document.DocumentCategory category = null;
        if (categoryStr != null && !categoryStr.isEmpty()) {
            try {
                category = Document.DocumentCategory.valueOf(categoryStr);
            } catch (IllegalArgumentException ignored) {}
        }

        // 날짜 변환
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (startDateStr != null && !startDateStr.isEmpty()) {
            startDate = LocalDate.parse(startDateStr, formatter).atStartOfDay();
        }
        if (endDateStr != null && !endDateStr.isEmpty()) {
            endDate = LocalDate.parse(endDateStr, formatter).atTime(23, 59, 59);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<DocumentResponse> documentPage = documentService.searchTeamDocuments(
                teamNo, userDetails.getUsername(),
                category, searchType, searchKeyword,
                startDate, endDate, pageable);

        model.addAttribute("documents", documentPage.getContent());
        model.addAttribute("page", documentPage);
        model.addAttribute("teamNo", teamNo);

        model.addAttribute("selectedCategory", categoryStr);
        model.addAttribute("selectedSearchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDateStr);

        return "document/list";
    }

    /* 문서 상세페이지 */
    @GetMapping("/{documentNo}")
    public String documentDetail (
            @PathVariable("documentNo") Long documentNo,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ) {

        DocumentDetailDTO document = documentService.getDocumentDetail(documentNo, userDetails.getUsername());

        // 관련 문서 추천
        List<RelatedDocumentDTO> relatedDocuments = documentService.getRelatedDocuments(documentNo, 5);

        List<DocumentCommentDTO> comments = documentCommentService.getCommentsByDocumentNo(documentNo);

        model.addAttribute("document", document);
        model.addAttribute("categories", Document.DocumentCategory.values());
        model.addAttribute("relatedDocuments", relatedDocuments);
        model.addAttribute("comments", comments);

        return "document/detail";
    }

    /* 문서 수정 */
    @PostMapping("/{documentNo}/update")
    @ResponseBody
    public ApiResponse<DocumentResponse> updateDocument(
            @PathVariable("documentNo") Long documentNo,
            @Valid @RequestBody DocumentUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        try {
            DocumentResponse response = documentService.updateDocument(documentNo, request, userDetails.getUsername());
            return ApiResponse.success(response);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /* 문서 삭제 (비활성화) */
    @PostMapping("/{documentNo}/delete")
    @ResponseBody
    public ApiResponse<Void> deleteDocument(
            @PathVariable("documentNo") Long documentNo,
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        try {
            documentService.deleteDocument(documentNo, userDetails.getUsername());
            return ApiResponse.success(null);
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(e.getMessage());
        }

    }

    /* 수동 재요약 */
    @PostMapping("/resummarize")
    @ResponseBody
    public ResummarizeResponse resummarize(@RequestParam(value = "documentNo") Long documentNo) {

        try {
            // 재요약 가능 여부 체크
            if (!documentSummaryService.canResummarize(documentNo)) {
                return new ResummarizeResponse(false, "이번 달 재요약 횟수를 모두 사용했습니다. (월 3회 제한)", 0);
            }

            documentSummaryService.resummarizeDocument(documentNo);

            // 남은 횟수
            int remaining = documentSummaryService.getRemainingResummaryCount(documentNo);

            return new ResummarizeResponse(true, "재요약이 완료되었습니다. 남은 횟수: ", remaining);

        } catch (IllegalArgumentException e) {
            return new ResummarizeResponse(false, e.getMessage(), 0);
        } catch (Exception e) {
            log.error("재요약 실패", e);
            return new ResummarizeResponse(false, "재요약에 실패했습니다. : " + e.getMessage(), 0);
        }
    }

    /* 재요약 응답 DTO */
    record ResummarizeResponse(boolean success, String message, int remainingCount) {}

    /* 댓글 작성 */
    @PostMapping("/{documentNo}/comment")
    @ResponseBody
    public ResponseEntity<ApiResponse<DocumentCommentDTO>> createComment (
            @PathVariable("documentNo") Long documentNo,
            @RequestBody Map<String, String> request
    ) {
        try {
            String content = request.get("content");

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("댓글 내용을 입력해주세요."));
            }

            DocumentCommentDTO comment = documentCommentService.createComment(documentNo, content);
            return ResponseEntity.ok(ApiResponse.success(comment));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("댓글 작성에 실패했습니다."));
        }
    }

    /* 댓글 수정 */
    @PutMapping("/comment/{commentNo}")
    @ResponseBody
    public ResponseEntity<ApiResponse<DocumentCommentDTO>> updateComment(
            @PathVariable("commentNo") Long commentNo,
            @RequestBody Map<String, String> request
    ) {
        try {
            String content = request.get("content");

            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("댓글 내용을 입력해주세요."));
            }

            DocumentCommentDTO comment = documentCommentService.updateComment(commentNo, content);
            return ResponseEntity.ok(ApiResponse.success(comment));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("댓글 수정에 실패했습니다."));
        }
    }

    /* 댓글 삭제 */
    @DeleteMapping("/comment/{commentNo}")
    @ResponseBody
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable("commentNo") Long commentNo
    ) {
        try {
            documentCommentService.deleteComment(commentNo);
            return ResponseEntity.ok(ApiResponse.success("댓글이 삭제되었습니다.", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("댓글 삭제에 실패했습니다."));
        }
    }
}
